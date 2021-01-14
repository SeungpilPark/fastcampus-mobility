package com.fastcampus.common.event.dispatch;

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
public class 배차시간초과됨 extends AbstractEvent {

  @NotNull
  private String operationId;

  @Override
  public String getCorrelationId() {
    return this.operationId;
  }

  @Builder
  public 배차시간초과됨(String operationId) {
    this.operationId = operationId;
    this.setEventTime(LocalDateTime.now());
  }
}
