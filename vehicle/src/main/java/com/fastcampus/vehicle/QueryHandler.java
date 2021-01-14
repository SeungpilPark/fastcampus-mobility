package com.fastcampus.vehicle;


import com.fastcampus.common.event.AbstractEvent;
import com.fastcampus.common.event.AbstractEventEntity;
import com.fastcampus.common.event.vehicle.승인대기로변경됨;
import com.fastcampus.common.event.vehicle.운행대기중변경됨;
import com.fastcampus.common.event.vehicle.운행중으로변경됨;
import com.fastcampus.common.event.vehicle.위치업데이트됨;
import com.fastcampus.common.event.vehicle.차량등록됨;
import com.fastcampus.common.exception.EntityNotFoundException;
import com.fastcampus.vehicle.domain.VehicleEntity;
import com.fastcampus.vehicle.domain.VehicleRepository;
import com.fastcampus.vehicle.domain.VehicleStatus;
import com.fastcampus.vehicle.eventstore.VehicleEventRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueryHandler {

  private final VehicleEventRepository vehicleEventRepository;
  private final VehicleRepository vehicleRepository;

  @Autowired
  public QueryHandler(
      final VehicleEventRepository vehicleEventRepository,
      final VehicleRepository vehicleRepository
  ) {
    this.vehicleEventRepository = vehicleEventRepository;
    this.vehicleRepository = vehicleRepository;
  }

  @Async
  public void project(차량등록됨 event) {
    log.info("프로젝션: {}", event.toString());
    vehicleRepository.save(this.apply(new VehicleEntity(), event));
  }

  @Async
  public void project(승인대기로변경됨 event) {
    log.info("프로젝션: {}", event.toString());
    vehicleRepository.save(this.apply(this.get(event.getVehicleId())
        .orElseThrow(EntityNotFoundException::new), event));
  }

  @Async
  public void project(운행대기중변경됨 event) {
    log.info("프로젝션: {}", event.toString());
    vehicleRepository.save(this.apply(this.get(event.getVehicleId())
        .orElseThrow(EntityNotFoundException::new), event));
  }

  @Async
  public void project(운행중으로변경됨 event) {
    log.info("프로젝션: {}", event.toString());
    vehicleRepository.save(this.apply(this.get(event.getVehicleId())
        .orElseThrow(EntityNotFoundException::new), event));
  }

  @Async
  public void project(위치업데이트됨 event) {
    log.info("프로젝션: {}", event.toString());
    vehicleRepository.save(this.apply(this.get(event.getVehicleId())
        .orElseThrow(EntityNotFoundException::new), event));
  }

  public VehicleEntity apply(VehicleEntity entity, 차량등록됨 event) {
    entity.setId(event.getVehicleId());
    entity.setCarNumber(event.getCarNumber());
    entity.setCoordinates(event.getCoordinates());
    entity.setStatus(VehicleStatus.운행대기중);
    return entity;
  }

  public VehicleEntity apply(VehicleEntity entity, 승인대기로변경됨 event) {
    entity.setId(event.getVehicleId());
    entity.setStatus(VehicleStatus.배차승인대기중);
    return entity;
  }

  public VehicleEntity apply(VehicleEntity entity, 운행대기중변경됨 event) {
    entity.setId(event.getVehicleId());
    entity.setStatus(VehicleStatus.운행대기중);
    return entity;
  }

  public VehicleEntity apply(VehicleEntity entity, 운행중으로변경됨 event) {
    entity.setId(event.getVehicleId());
    entity.setStatus(VehicleStatus.운행중);
    return entity;
  }

  public VehicleEntity apply(VehicleEntity entity, 위치업데이트됨 event) {
    entity.setId(event.getVehicleId());
    entity.setCoordinates(event.getCoordinates());
    return entity;
  }

  public Optional<VehicleEntity> get(final String vehicleId) {
    List<AbstractEvent> eventList =
        vehicleEventRepository.findAllByCorrelationId(vehicleId)
            .stream()
            .map(AbstractEventEntity::toEvent)
            .sorted(Comparator.comparing(AbstractEvent::getEventTime))
            .collect(Collectors.toList());
    if (eventList.isEmpty()) {
      return Optional.empty();
    }

    VehicleEntity entity = new VehicleEntity();
    eventList.forEach(event -> {
      if (event instanceof 차량등록됨) {
        this.apply(entity, (차량등록됨) event);
      } else if (event instanceof 승인대기로변경됨) {
        this.apply(entity, (승인대기로변경됨) event);
      } else if (event instanceof 운행대기중변경됨) {
        this.apply(entity, (운행대기중변경됨) event);
      } else if (event instanceof 운행중으로변경됨) {
        this.apply(entity, (운행중으로변경됨) event);
      } else if (event instanceof 위치업데이트됨) {
        this.apply(entity, (위치업데이트됨) event);
      }
    });
    return Optional.of(entity);
  }
}
