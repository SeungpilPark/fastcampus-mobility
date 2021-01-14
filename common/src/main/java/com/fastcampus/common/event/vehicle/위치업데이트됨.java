package com.fastcampus.common.event.vehicle;

import com.fastcampus.common.event.AbstractEvent;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class 위치업데이트됨 extends AbstractEvent {

  @NotNull
  private String operationId;
  @NotNull
  private String vehicleId;
  @NotEmpty
  private String coordinates;
  private boolean originPassed;
  private boolean passengerPassed;
  private boolean destinationPassed;

  @Override
  public String getCorrelationId() {
    return this.vehicleId;
  }

  @Builder
  public 위치업데이트됨(String operationId, String vehicleId, String coordinates,
      boolean originPassed, boolean passengerPassed, boolean destinationPassed) {
    this.operationId = operationId;
    this.vehicleId = vehicleId;
    this.coordinates = coordinates;
    this.originPassed = originPassed;
    this.passengerPassed = passengerPassed;
    this.destinationPassed = destinationPassed;
    this.setEventTime(LocalDateTime.now());
  }
}
