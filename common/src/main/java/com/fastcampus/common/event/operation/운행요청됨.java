package com.fastcampus.common.event.operation;

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
public class 운행요청됨 extends AbstractEvent {

  @NotNull
  private String operationId;
  @NotEmpty
  private String passengerCoordinates;
  @NotEmpty
  private String destinationCoordinates;

  @Override
  public String getCorrelationId() {
    return this.operationId;
  }

  @Builder
  public 운행요청됨(String operationId, String passengerCoordinates, String destinationCoordinates) {
    this.operationId = operationId;
    this.passengerCoordinates = passengerCoordinates;
    this.destinationCoordinates = destinationCoordinates;
    this.setEventTime(LocalDateTime.now());
  }
}
