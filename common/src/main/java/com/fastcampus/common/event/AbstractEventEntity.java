package com.fastcampus.common.event;

import static com.fastcampus.common.util.ObjectUtils.createObjectMapper;

import com.fastcampus.common.event.dispatch.배차미승인됨;
import com.fastcampus.common.event.dispatch.배차승인됨;
import com.fastcampus.common.event.dispatch.배차시간초과됨;
import com.fastcampus.common.event.dispatch.배차추천됨;
import com.fastcampus.common.event.dispatch.배차취소됨;
import com.fastcampus.common.event.operation.승객탑승됨;
import com.fastcampus.common.event.operation.운행시작됨;
import com.fastcampus.common.event.operation.운행요청됨;
import com.fastcampus.common.event.operation.운행종료됨;
import com.fastcampus.common.event.operation.운행취소됨;
import com.fastcampus.common.event.vehicle.승인대기로변경됨;
import com.fastcampus.common.event.vehicle.운행대기중변경됨;
import com.fastcampus.common.event.vehicle.운행중으로변경됨;
import com.fastcampus.common.event.vehicle.위치계산요청됨;
import com.fastcampus.common.event.vehicle.위치업데이트됨;
import com.fastcampus.common.event.vehicle.차량등록됨;
import com.fastcampus.common.exception.EventBindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AbstractEventEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(name = "correlation_id", nullable = false)
  private String correlationId;

  @Column(name = "payload", nullable = false)
  private String payload;

  @CreatedDate
  @Column(name = "create_date", nullable = false, updatable = false)
  protected LocalDateTime createDate;

  public AbstractEvent toEvent() {
    ObjectMapper objectMapper = createObjectMapper();
    try {
      String eventName = Optional.ofNullable(JsonPath.read(this.payload, "$.eventName"))
          .map(name -> (String) name)
          .orElseThrow(() -> new EventBindException("이벤트스토어 내용안에 이벤트 이름이 없습니다."));
      switch (eventName) {
        case "배차미승인됨":
          return objectMapper.readValue(this.payload, 배차미승인됨.class);
        case "배차승인됨":
          return objectMapper.readValue(this.payload, 배차승인됨.class);
        case "배차시간초과됨":
          return objectMapper.readValue(this.payload, 배차시간초과됨.class);
        case "배차추천됨":
          return objectMapper.readValue(this.payload, 배차추천됨.class);
        case "배차취소됨":
          return objectMapper.readValue(this.payload, 배차취소됨.class);
        case "승객탑승됨":
          return objectMapper.readValue(this.payload, 승객탑승됨.class);
        case "운행시작됨":
          return objectMapper.readValue(this.payload, 운행시작됨.class);
        case "운행요청됨":
          return objectMapper.readValue(this.payload, 운행요청됨.class);
        case "운행종료됨":
          return objectMapper.readValue(this.payload, 운행종료됨.class);
        case "운행취소됨":
          return objectMapper.readValue(this.payload, 운행취소됨.class);
        case "차량등록됨":
          return objectMapper.readValue(this.payload, 차량등록됨.class);
        case "승인대기로변경됨":
          return objectMapper.readValue(this.payload, 승인대기로변경됨.class);
        case "운행대기중변경됨":
          return objectMapper.readValue(this.payload, 운행대기중변경됨.class);
        case "운행중으로변경됨":
          return objectMapper.readValue(this.payload, 운행중으로변경됨.class);
        case "위치계산요청됨":
          return objectMapper.readValue(this.payload, 위치계산요청됨.class);
        case "위치업데이트됨":
          return objectMapper.readValue(this.payload, 위치업데이트됨.class);
        default:
          throw new EventBindException(String.format("%s 이벤트 클래스를 찾을 수 없습니다.", eventName));
      }
    } catch (IOException jpe) {
      throw new EventBindException("이벤트스토어 엔티티를 이벤트로 변환할 수 없습니다.", jpe);
    }
  }
}

