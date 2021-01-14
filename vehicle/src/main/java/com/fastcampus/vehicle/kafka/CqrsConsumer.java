package com.fastcampus.vehicle.kafka;

import static com.fastcampus.common.util.ObjectUtils.createObjectMapper;

import com.fastcampus.common.event.vehicle.승인대기로변경됨;
import com.fastcampus.common.event.vehicle.운행대기중변경됨;
import com.fastcampus.common.event.vehicle.운행중으로변경됨;
import com.fastcampus.common.event.vehicle.위치업데이트됨;
import com.fastcampus.common.event.vehicle.차량등록됨;
import com.fastcampus.common.exception.EventBindException;
import com.fastcampus.vehicle.QueryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CqrsConsumer {

  private final QueryHandler projectionHandler;

  @Autowired
  public CqrsConsumer(final QueryHandler projectionHandler) {
    this.projectionHandler = projectionHandler;
  }

  @StreamListener(KafkaStreams.CQRS_INPUT)
  public void consumer(@Payload String payload) {

    log.info("{} Received Cqrs", payload);
    ObjectMapper objectMapper = createObjectMapper();
    try {
      String eventName = Optional.ofNullable(JsonPath.read(payload, "$.eventName"))
          .map(name -> (String) name)
          .orElseThrow(() -> new EventBindException("CQRS 메시지에 이벤트 이름이 없습니다."));
      switch (eventName) {
        case "차량등록됨":
          projectionHandler.project(objectMapper.readValue(payload, 차량등록됨.class));
          break;
        case "승인대기로변경됨":
          projectionHandler.project(objectMapper.readValue(payload, 승인대기로변경됨.class));
          break;
        case "운행대기중변경됨":
          projectionHandler.project(objectMapper.readValue(payload, 운행대기중변경됨.class));
          break;
        case "운행중으로변경됨":
          projectionHandler.project(objectMapper.readValue(payload, 운행중으로변경됨.class));
          break;
        case "위치업데이트됨":
          projectionHandler.project(objectMapper.readValue(payload, 위치업데이트됨.class));
          break;
      }
    } catch (IOException jpe) {
      throw new EventBindException("CQRS 메시지를 이벤트로 변환할 수 없습니다.", jpe);
    }
  }
}
