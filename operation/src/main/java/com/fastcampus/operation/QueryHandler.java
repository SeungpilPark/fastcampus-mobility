package com.fastcampus.operation;


import com.fastcampus.common.event.AbstractEvent;
import com.fastcampus.common.event.AbstractEventEntity;
import com.fastcampus.common.event.operation.승객탑승됨;
import com.fastcampus.common.event.operation.운행시작됨;
import com.fastcampus.common.event.operation.운행요청됨;
import com.fastcampus.common.event.operation.운행종료됨;
import com.fastcampus.common.event.operation.운행취소됨;
import com.fastcampus.common.exception.EntityNotFoundException;
import com.fastcampus.operation.domain.OperationEntity;
import com.fastcampus.operation.domain.OperationRepository;
import com.fastcampus.operation.domain.OperationStatus;
import com.fastcampus.operation.eventstore.OperationEventRepository;
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

  private final OperationEventRepository operationEventRepository;
  private final OperationRepository operationRepository;

  @Autowired
  public QueryHandler(
      final OperationEventRepository operationEventRepository,
      final OperationRepository operationRepository
  ) {
    this.operationEventRepository = operationEventRepository;
    this.operationRepository = operationRepository;
  }

  @Async
  public void project(운행요청됨 event) {
    log.info("프로젝션: {}", event.toString());
    operationRepository.save(this.apply(new OperationEntity(), event));
  }

  @Async
  public void project(운행시작됨 event) {
    log.info("프로젝션: {}", event.toString());
    operationRepository.save(this.apply(this.get(event.getOperationId())
        .orElseThrow(EntityNotFoundException::new), event));
  }

  @Async
  public void project(승객탑승됨 event) {
    log.info("프로젝션: {}", event.toString());
    operationRepository.save(this.apply(this.get(event.getOperationId())
        .orElseThrow(EntityNotFoundException::new), event));
  }

  @Async
  public void project(운행종료됨 event) {
    log.info("프로젝션: {}", event.toString());
    operationRepository.save(this.apply(this.get(event.getOperationId())
        .orElseThrow(EntityNotFoundException::new), event));
  }

  @Async
  public void project(운행취소됨 event) {
    log.info("프로젝션: {}", event.toString());
    operationRepository.save(this.apply(this.get(event.getOperationId())
        .orElseThrow(EntityNotFoundException::new), event));
  }

  public OperationEntity apply(OperationEntity entity, 운행요청됨 event) {
    entity.setId(event.getOperationId());
    entity.setPassengerCoordinates(event.getPassengerCoordinates());
    entity.setDestinationCoordinates(event.getDestinationCoordinates());
    entity.setStatus(OperationStatus.운행요청중);
    return entity;
  }

  public OperationEntity apply(OperationEntity entity, 운행시작됨 event) {
    entity.setId(event.getOperationId());
    entity.setStatus(OperationStatus.운행중);
    return entity;
  }

  public OperationEntity apply(OperationEntity entity, 승객탑승됨 event) {
    entity.setId(event.getOperationId());
    entity.setStatus(OperationStatus.승객탑승중);
    return entity;
  }

  public OperationEntity apply(OperationEntity entity, 운행종료됨 event) {
    entity.setId(event.getOperationId());
    entity.setStatus(OperationStatus.운행종료);
    return entity;
  }

  public OperationEntity apply(OperationEntity entity, 운행취소됨 event) {
    entity.setId(event.getOperationId());
    entity.setStatus(OperationStatus.운행취소);
    return entity;
  }

  public Optional<OperationEntity> get(final String operationId) {
    List<AbstractEvent> eventList =
        operationEventRepository.findAllByCorrelationId(operationId)
            .stream()
            .map(AbstractEventEntity::toEvent)
            .sorted(Comparator.comparing(AbstractEvent::getEventTime))
            .collect(Collectors.toList());
    if (eventList.isEmpty()) {
      return Optional.empty();
    }

    OperationEntity entity = new OperationEntity();
    eventList.forEach(event -> {
      if (event instanceof 운행요청됨) {
        this.apply(entity, (운행요청됨) event);
      } else if (event instanceof 운행시작됨) {
        this.apply(entity, (운행시작됨) event);
      } else if (event instanceof 승객탑승됨) {
        this.apply(entity, (승객탑승됨) event);
      } else if (event instanceof 운행종료됨) {
        this.apply(entity, (운행종료됨) event);
      } else if (event instanceof 운행취소됨) {
        this.apply(entity, (운행취소됨) event);
      }
    });
    return Optional.of(entity);
  }
}
