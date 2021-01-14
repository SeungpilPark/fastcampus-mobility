package com.fastcampus.vehicle;

import com.fastcampus.common.event.AbstractEvent;
import com.fastcampus.common.event.AbstractEventEntity;
import com.fastcampus.common.event.dispatch.배차미승인됨;
import com.fastcampus.common.event.dispatch.배차승인됨;
import com.fastcampus.common.event.dispatch.배차추천됨;
import com.fastcampus.common.event.dispatch.배차취소됨;
import com.fastcampus.common.event.vehicle.승인대기로변경됨;
import com.fastcampus.common.event.vehicle.운행대기중변경됨;
import com.fastcampus.common.event.vehicle.운행중으로변경됨;
import com.fastcampus.common.event.vehicle.위치계산요청됨;
import com.fastcampus.common.event.vehicle.위치업데이트됨;
import com.fastcampus.common.event.vehicle.차량등록됨;
import com.fastcampus.common.exception.BusinessException;
import com.fastcampus.common.exception.EntityNotFoundException;
import com.fastcampus.vehicle.command.AddCommand;
import com.fastcampus.vehicle.domain.VehicleEntity;
import com.fastcampus.vehicle.domain.VehicleRepository;
import com.fastcampus.vehicle.domain.VehicleRouteEntity;
import com.fastcampus.vehicle.domain.VehicleRouteRepository;
import com.fastcampus.vehicle.domain.VehicleStatus;
import com.fastcampus.vehicle.eventstore.VehicleEventEntity;
import com.fastcampus.vehicle.eventstore.VehicleEventRepository;
import com.fastcampus.vehicle.external.OperationClient;
import com.fastcampus.vehicle.kafka.CqrsSender;
import com.fastcampus.vehicle.kafka.EventSender;
import com.fastcampus.vehicle.map.MapService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@Validated
public class CommandHandler {

  private final QueryHandler queryHandler;
  private final VehicleEventRepository vehicleEventRepository;
  private final VehicleRepository vehicleRepository;
  private final EventSender eventSender;
  private final CqrsSender cqrsSender;
  private final MapService mapService;
  private final VehicleRouteRepository vehicleRouteRepository;
  private final OperationClient operationClient;

  @Autowired
  public CommandHandler(
      final QueryHandler queryHandler,
      final VehicleEventRepository vehicleEventRepository,
      final VehicleRepository vehicleRepository,
      final EventSender eventSender,
      final CqrsSender cqrsSender,
      final MapService mapService,
      final VehicleRouteRepository vehicleRouteRepository,
      final OperationClient operationClient
  ) {
    this.queryHandler = queryHandler;
    this.vehicleEventRepository = vehicleEventRepository;
    this.vehicleRepository = vehicleRepository;
    this.eventSender = eventSender;
    this.cqrsSender = cqrsSender;
    this.mapService = mapService;
    this.vehicleRouteRepository = vehicleRouteRepository;
    this.operationClient = operationClient;
  }

  public VehicleEntity execute(@Valid final AddCommand vehicleAddCommand) {
    vehicleRepository.findByCarNumber(vehicleAddCommand.getCarNumber())
        .ifPresent(entity -> {
          throw new BusinessException("이미 등록된 차량번호 입니다.");
        });
    차량등록됨 nextEvent = 차량등록됨.builder()
        .vehicleId(UUID.randomUUID().toString())
        .carNumber(vehicleAddCommand.getCarNumber())
        .coordinates(vehicleAddCommand.getCoordinates())
        .build();

    vehicleEventRepository.save(nextEvent.toEventEntity(VehicleEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
    return queryHandler.apply(new VehicleEntity(), nextEvent);
  }

  public void execute(@Valid final 배차추천됨 event) {
    if (!VehicleStatus.운행대기중.equals(
        queryHandler.get(event.getVehicleId())
            .orElseThrow(EntityNotFoundException::new).getStatus())) {
      log.warn("운행대기중 아니어서 배차 거절, {}", event.toString());
      return;
    }

    // 절반의 확률로 수락
    if (new Random().nextBoolean()) {
      log.warn("배차 거절, {}", event.toString());
      return;
    }

    log.warn("배차 수락, {}", event.toString());
    승인대기로변경됨 nextEvent = 승인대기로변경됨.builder()
        .operationId(event.getOperationId())
        .vehicleId(event.getVehicleId())
        .build();

    vehicleEventRepository.save(nextEvent.toEventEntity(VehicleEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
    eventSender.sendMessage(nextEvent);
  }


  public void execute(@Valid final 배차승인됨 event) {
    // 운행정보 가져오기
    Map operation = operationClient.get(event.getOperationId());

    // 경로계산
    VehicleEntity entity = vehicleRepository.findById(event.getVehicleId())
        .orElseThrow(EntityNotFoundException::new);
    VehicleRouteEntity vehicleRouteEntity =
        mapService.getRoute(entity.getCoordinates(),
            operation.get("passengerCoordinates").toString(),
            operation.get("destinationCoordinates").toString());

    // 경로저장
    vehicleRouteEntity.setOperationId(event.getOperationId());
    vehicleRouteEntity.setVehicleId(event.getVehicleId());
    vehicleRouteRepository.save(vehicleRouteEntity);

    운행중으로변경됨 nextEvent = 운행중으로변경됨.builder()
        .operationId(event.getOperationId())
        .vehicleId(event.getVehicleId())
        .build();
    vehicleEventRepository.save(nextEvent.toEventEntity(VehicleEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
    eventSender.sendMessage(nextEvent);
  }

  public void execute(@Valid final 배차미승인됨 event) {
    운행대기중변경됨 nextEvent = 운행대기중변경됨.builder()
        .operationId(event.getOperationId())
        .vehicleId(event.getVehicleId())
        .build();
    vehicleEventRepository.save(nextEvent.toEventEntity(VehicleEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
  }

  public void execute(@Valid final 배차취소됨 event) {
    운행대기중변경됨 nextEvent = 운행대기중변경됨.builder()
        .operationId(event.getOperationId())
        .vehicleId(event.getVehicleId())
        .build();
    vehicleEventRepository.save(nextEvent.toEventEntity(VehicleEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
  }

  public void execute(@Valid final 위치계산요청됨 event) {
    VehicleRouteEntity vehicleRouteEntity = vehicleRouteRepository
        .findByOperationIdAndVehicleId(event.getOperationId(), event.getVehicleId())
        .orElseThrow(EntityNotFoundException::new);

    LocalDateTime operationStartTime = vehicleEventRepository
        .findAllByCorrelationId(event.getVehicleId())
        .stream()
        .map(AbstractEventEntity::toEvent)
        .sorted(Comparator.comparing(AbstractEvent::getEventTime).reversed())
        .filter(e -> e instanceof 운행중으로변경됨)
        .map(e -> (운행중으로변경됨) e)
        .filter(e -> e.getOperationId().equals(event.getOperationId()))
        .findFirst()
        .map(AbstractEvent::getEventTime)
        .orElseThrow(EntityNotFoundException::new);

    long elapsedTimeSeconds = ChronoUnit.SECONDS.between(operationStartTime, LocalDateTime.now());
    위치업데이트됨 nextEvent = mapService
        .getCurrentVehicleCoordinates(vehicleRouteEntity, elapsedTimeSeconds);
    nextEvent.setEventTime(LocalDateTime.now());
    nextEvent.setOperationId(event.getOperationId());
    nextEvent.setVehicleId(event.getVehicleId());

    vehicleEventRepository.save(nextEvent.toEventEntity(VehicleEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
    eventSender.sendMessage(nextEvent);
  }
}
