package com.fastcampus.vehicle.domain;

import com.fastcampus.common.event.AbstractDomainEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "vehicle")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class VehicleEntity extends AbstractDomainEntity {

  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "car_number", nullable = false)
  private String carNumber;

  @Column(name = "coordinates", nullable = false)
  private String coordinates;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private VehicleStatus status;

  @Builder
  public VehicleEntity(String id, String carNumber, String coordinates,
      VehicleStatus status) {
    this.id = id;
    this.carNumber = carNumber;
    this.coordinates = coordinates;
    this.status = status;
  }
}

