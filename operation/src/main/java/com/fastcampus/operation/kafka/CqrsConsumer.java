package com.fastcampus.operation.kafka;

import static com.fastcampus.common.util.ObjectUtils.createObjectMapper;

import com.fastcampus.common.event.operation.승객탑승됨;
import com.fastcampus.common.event.operation.운행시작됨;
import com.fastcampus.common.event.operation.운행요청됨;
import com.fastcampus.common.event.operation.운행종료됨;
import com.fastcampus.common.event.operation.운행취소됨;
import com.fastcampus.common.exception.EventBindException;
import com.fastcampus.operation.QueryHandler;
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

  private final QueryHandler queryHandler;

  @Autowired
  public CqrsConsumer(final QueryHandler queryHandler) {
    this.queryHandler = queryHandler;
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
        case "운행요청됨":
          queryHandler.project(objectMapper.readValue(payload, 운행요청됨.class));
          break;
        case "운행시작됨":
          queryHandler.project(objectMapper.readValue(payload, 운행시작됨.class));
          break;
        case "승객탑승됨":
          queryHandler.project(objectMapper.readValue(payload, 승객탑승됨.class));
          break;
        case "운행종료됨":
          queryHandler.project(objectMapper.readValue(payload, 운행종료됨.class));
          break;
        case "운행취소됨":
          queryHandler.project(objectMapper.readValue(payload, 운행취소됨.class));
          break;
      }
    } catch (IOException jpe) {
      throw new EventBindException("CQRS 메시지를 이벤트로 변환할 수 없습니다.", jpe);
    }
  }
}
