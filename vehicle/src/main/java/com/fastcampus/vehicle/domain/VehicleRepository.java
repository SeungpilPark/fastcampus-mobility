package com.fastcampus.vehicle.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {

  Optional<VehicleEntity> findByCarNumber(String carNumber);
}
