package com.fastcampus.vehicle.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRouteRepository extends JpaRepository<VehicleRouteEntity, Long> {

  Optional<VehicleRouteEntity> findByOperationIdAndVehicleId(final String operationId,
      final String vehicleId);
}
