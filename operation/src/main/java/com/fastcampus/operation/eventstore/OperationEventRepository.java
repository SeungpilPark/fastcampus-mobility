package com.fastcampus.operation.eventstore;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationEventRepository extends JpaRepository<OperationEventEntity, Long> {

  List<OperationEventEntity> findAllByCorrelationId(final String correlationId);
}
