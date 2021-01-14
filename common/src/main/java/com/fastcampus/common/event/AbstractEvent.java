package com.fastcampus.common.event;

import static com.fastcampus.common.util.ObjectUtils.createObjectMapper;

import com.fastcampus.common.exception.EventBindException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

public abstract class AbstractEvent implements Serializable, EventInterface {

  private static final long serialVersionUID = 1L;

  @NotNull
  private LocalDateTime eventTime;

  public String getEventName() {
    return this.getClass().getSimpleName();
  }

  public LocalDateTime getEventTime() {
    return eventTime;
  }

  public void setEventTime(LocalDateTime eventTime) {
    this.eventTime = eventTime;
  }

  public String toJson() {
    ObjectMapper objectMapper = createObjectMapper();
    try {
      return objectMapper.writeValueAsString(this);
    } catch (IOException ioe) {
      throw new EventBindException("이벤트를 JSON 으로 변환할 수 없습니다", ioe);
    }
  }

  public <T extends AbstractEventEntity> T toEventEntity(final Class<T> entityType) {
    T entityInstance = BeanUtils.instantiateClass(entityType);
    ObjectMapper objectMapper = createObjectMapper();
    try {
      String payload = objectMapper.writeValueAsString(this);
      entityInstance.setPayload(payload);
      entityInstance.setCorrelationId(
          Optional.ofNullable(this.getCorrelationId())
              .orElseThrow(() -> new EventBindException(
                  String.format("%s 이벤트의 코릴레이션 키가 없습니다.", this.getEventName())))
      );
      return entityInstance;
    } catch (IOException ioe) {
      throw new EventBindException("이벤트 스토어 엔티티로 변환할 수 없습니다", ioe);
    }
  }
}

interface EventInterface {

  @JsonIgnore
  public String getCorrelationId();
}
