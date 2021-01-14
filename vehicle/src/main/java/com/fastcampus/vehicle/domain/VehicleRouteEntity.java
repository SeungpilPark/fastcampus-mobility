package com.fastcampus.vehicle.domain;

import com.fastcampus.common.event.AbstractDomainEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "vehicle_route")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class VehicleRouteEntity extends AbstractDomainEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(name = "vehicle_id", nullable = false)
  private String vehicleId;

  @Column(name = "operation_id", nullable = false)
  private String operationId;

  @Column(name = "paths", nullable = false)
  private String paths;

  @Column(name = "origin_coordinates", nullable = false)
  private String originCoordinates;

  @Column(name = "passenger_coordinates", nullable = false)
  private String passengerCoordinates;

  @Column(name = "passenger_path_index", nullable = false)
  private int passengerPathIndex;

  @Column(name = "destination_coordinates", nullable = false)
  private String destinationCoordinates;

  @Builder
  public VehicleRouteEntity(long id, String vehicleId, String operationId, String paths,
      String originCoordinates, String passengerCoordinates, int passengerPathIndex,
      String destinationCoordinates) {
    this.id = id;
    this.vehicleId = vehicleId;
    this.operationId = operationId;
    this.paths = paths;
    this.originCoordinates = originCoordinates;
    this.passengerCoordinates = passengerCoordinates;
    this.passengerPathIndex = passengerPathIndex;
    this.destinationCoordinates = destinationCoordinates;
  }
}

