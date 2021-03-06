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
public class 운행취소됨 extends AbstractEvent {

  @NotNull
  private String operationId;

  @Override
  public String getCorrelationId() {
    return this.operationId;
  }

  @Builder
  public 운행취소됨(String operationId) {
    this.operationId = operationId;
    this.setEventTime(LocalDateTime.now());
  }
}
