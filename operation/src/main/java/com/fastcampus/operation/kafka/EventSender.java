package com.fastcampus.operation.kafka;

import com.fastcampus.common.event.AbstractEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
@Slf4j
public class EventSender {

  private final KafkaStreams kafkaStreams;

  @Autowired
  public EventSender(
      final KafkaStreams kafkaStreams) {
    this.kafkaStreams = kafkaStreams;
  }

  public void sendMessage(final AbstractEvent abstractEvent) {
    log.info("{}, Send Event ", abstractEvent.toString());
    kafkaStreams.domainOutboundTopic().send(MessageBuilder
        .withPayload(abstractEvent.toJson())
        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
        .build());
  }
}
