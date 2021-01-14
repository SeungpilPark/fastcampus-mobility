package com.fastcampus.common.event.vehicle;

import com.fastcampus.common.event.AbstractEvent;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class 차량등록됨 extends AbstractEvent {

  private String vehicleId;
  private String carNumber;
  private String coordinates;

  @Override
  public String getCorrelationId() {
    return this.vehicleId;
  }

  @Builder
  public 차량등록됨(String vehicleId, String carNumber, String coordinates) {
    this.vehicleId = vehicleId;
    this.carNumber = carNumber;
    this.coordinates = coordinates;
    this.setEventTime(LocalDateTime.now());
  }
}
