package com.fastcampus.operation;

import com.fastcampus.common.event.dispatch.배차시간초과됨;
import com.fastcampus.common.event.operation.승객탑승됨;
import com.fastcampus.common.event.operation.운행시작됨;
import com.fastcampus.common.event.operation.운행요청됨;
import com.fastcampus.common.event.operation.운행종료됨;
import com.fastcampus.common.event.operation.운행취소됨;
import com.fastcampus.common.event.vehicle.운행중으로변경됨;
import com.fastcampus.common.event.vehicle.위치업데이트됨;
import com.fastcampus.common.exception.BusinessException;
import com.fastcampus.common.exception.EntityNotFoundException;
import com.fastcampus.operation.command.AddCommand;
import com.fastcampus.operation.command.CancelCommand;
import com.fastcampus.operation.domain.OperationEntity;
import com.fastcampus.operation.domain.OperationRepository;
import com.fastcampus.operation.domain.OperationStatus;
import com.fastcampus.operation.eventstore.OperationEventEntity;
import com.fastcampus.operation.eventstore.OperationEventRepository;
import com.fastcampus.operation.kafka.CqrsSender;
import com.fastcampus.operation.kafka.EventSender;
import java.util.Arrays;
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
  private final OperationEventRepository operationEventRepository;
  private final OperationRepository operationRepository;
  private final EventSender eventSender;
  private final CqrsSender cqrsSender;

  @Autowired
  public CommandHandler(
      final QueryHandler queryHandler,
      final OperationEventRepository operationEventRepository,
      final OperationRepository operationRepository,
      final EventSender eventSender,
      final CqrsSender cqrsSender
  ) {
    this.queryHandler = queryHandler;
    this.operationEventRepository = operationEventRepository;
    this.operationRepository = operationRepository;
    this.eventSender = eventSender;
    this.cqrsSender = cqrsSender;
  }

  public OperationEntity execute(@Valid final AddCommand addCommand) {
    운행요청됨 nextEvent = 운행요청됨.builder()
        .operationId(UUID.randomUUID().toString())
        .passengerCoordinates(addCommand.getPassengerCoordinates())
        .destinationCoordinates(addCommand.getDestinationCoordinates())
        .build();

    operationEventRepository.save(nextEvent.toEventEntity(OperationEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
    eventSender.sendMessage(nextEvent);
    return queryHandler.apply(new OperationEntity(), nextEvent);
  }

  public void execute(@Valid final 운행중으로변경됨 event) {
    운행시작됨 nextEvent = 운행시작됨.builder()
        .operationId(event.getOperationId())
        .vehicleId(event.getVehicleId())
        .build();
    operationEventRepository.save(nextEvent.toEventEntity(OperationEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
  }

  public void execute(@Valid final 위치업데이트됨 event) {
    OperationEntity operationEntity = queryHandler.get(event.getOperationId())
        .orElseThrow(EntityNotFoundException::new);

    if ((event.isPassengerPassed() && !event.isDestinationPassed()) &&
        !OperationStatus.승객탑승중.equals(operationEntity.getStatus())) {
      승객탑승됨 nextEvent = 승객탑승됨.builder()
          .operationId(event.getOperationId())
          .vehicleId(event.getVehicleId())
          .build();
      operationEventRepository.save(nextEvent.toEventEntity(OperationEventEntity.class));
      cqrsSender.sendMessage(nextEvent);
    } else if ((event.isPassengerPassed() && event.isDestinationPassed()) &&
        !OperationStatus.운행종료.equals(operationEntity.getStatus())) {
      운행종료됨 nextEvent = 운행종료됨.builder()
          .operationId(event.getOperationId())
          .vehicleId(event.getVehicleId())
          .build();
      operationEventRepository.save(nextEvent.toEventEntity(OperationEventEntity.class));
      cqrsSender.sendMessage(nextEvent);
      eventSender.sendMessage(nextEvent);
    }
  }

  public OperationEntity execute(@Valid final CancelCommand cancelCommand) {
    return this.cancelOperation(cancelCommand.getOperationId());
  }

  public OperationEntity execute(@Valid final 배차시간초과됨 event) {
    return this.cancelOperation(event.getOperationId());
  }

  public OperationEntity cancelOperation(String operationId) {
    OperationEntity operationEntity = queryHandler.get(operationId)
        .orElseThrow(EntityNotFoundException::new);

    OperationStatus[] invalidStatus = new OperationStatus[]{
        OperationStatus.승객탑승중,
        OperationStatus.운행종료,
        OperationStatus.운행취소
    };
    if (Arrays.asList(invalidStatus).contains(operationEntity.getStatus())) {
      throw new BusinessException(
          String.format("%s 상태에서는 취소할 수 없습니다.", operationEntity.getStatus().name()));
    }
    운행취소됨 nextEvent = 운행취소됨.builder()
        .operationId(UUID.randomUUID().toString())
        .build();
    operationEventRepository.save(nextEvent.toEventEntity(OperationEventEntity.class));
    cqrsSender.sendMessage(nextEvent);
    eventSender.sendMessage(nextEvent);
    return queryHandler.apply(operationEntity, nextEvent);
  }
}
