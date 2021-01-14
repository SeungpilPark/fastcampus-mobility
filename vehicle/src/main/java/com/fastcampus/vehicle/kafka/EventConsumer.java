package com.fastcampus.vehicle.kafka;

import static com.fastcampus.common.util.ObjectUtils.createObjectMapper;

import com.fastcampus.common.event.dispatch.배차미승인됨;
import com.fastcampus.common.event.dispatch.배차승인됨;
import com.fastcampus.common.event.dispatch.배차추천됨;
import com.fastcampus.common.event.dispatch.배차취소됨;
import com.fastcampus.common.event.vehicle.위치계산요청됨;
import com.fastcampus.common.exception.EventBindException;
import com.fastcampus.vehicle.CommandHandler;
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
public class EventConsumer {

  private final CommandHandler commandHandler;

  @Autowired
  public EventConsumer(final CommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  @StreamListener(KafkaStreams.DOMAIN_INPUT)
  public void consumer(@Payload String payload) {
    log.info("{} Received Event", payload);
    ObjectMapper objectMapper = createObjectMapper();
    try {
      String eventName = Optional.ofNullable(JsonPath.read(payload, "$.eventName"))
          .map(name -> (String) name)
          .orElseThrow(() -> new EventBindException("도메인 메시지에 이벤트 이름이 없습니다."));
      switch (eventName) {
        case "배차추천됨":
          commandHandler.execute(objectMapper.readValue(payload, 배차추천됨.class));
          break;
        case "배차승인됨":
          commandHandler.execute(objectMapper.readValue(payload, 배차승인됨.class));
          break;
        case "배차미승인됨":
          commandHandler.execute(objectMapper.readValue(payload, 배차미승인됨.class));
          break;
        case "배차취소됨":
          commandHandler.execute(objectMapper.readValue(payload, 배차취소됨.class));
          break;
        case "위치계산요청됨":
          commandHandler.execute(objectMapper.readValue(payload, 위치계산요청됨.class));
          break;
      }
    } catch (IOException jpe) {
      throw new EventBindException("도메인 메시지를 이벤트로 변환할 수 없습니다.", jpe);
    }
  }
}
