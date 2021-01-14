package com.fastcampus.common.event.operation;

import com.fastcampus.common.event.AbstractEvent;
import java.time.LocalDateTime;
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
public class 운행종료됨 extends AbstractEvent {

  @NotNull
  private String operationId;
  @NotNull
  private String vehicleId;

  @Override
  public String getCorrelationId() {
    return this.operationId;
  }

  @Builder
  public 운행종료됨(String operationId, String vehicleId) {
    this.operationId = operationId;
    this.vehicleId = vehicleId;
    this.setEventTime(LocalDateTime.now());
  }
}
