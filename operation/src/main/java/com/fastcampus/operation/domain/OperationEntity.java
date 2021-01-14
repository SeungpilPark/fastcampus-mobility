package com.fastcampus.operation.domain;

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
@Entity(name = "operation")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OperationEntity extends AbstractDomainEntity {

  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "passenger_coordinates", nullable = false)
  private String passengerCoordinates;

  @Column(name = "destination_coordinates", nullable = false)
  private String destinationCoordinates;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private OperationStatus status;

  @Builder
  public OperationEntity(String id, String passengerCoordinates,
      String destinationCoordinates, OperationStatus status) {
    this.id = id;
    this.passengerCoordinates = passengerCoordinates;
    this.destinationCoordinates = destinationCoordinates;
    this.status = status;
  }
}

