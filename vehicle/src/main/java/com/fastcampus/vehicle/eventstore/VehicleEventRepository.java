package com.fastcampus.vehicle.eventstore;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleEventRepository extends JpaRepository<VehicleEventEntity, Long> {

  List<VehicleEventEntity> findAllByCorrelationId(final String correlationId);
}
