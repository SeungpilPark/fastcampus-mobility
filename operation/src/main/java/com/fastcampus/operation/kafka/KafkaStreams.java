package com.fastcampus.operation.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface KafkaStreams {

  String CQRS_INPUT = "cqrs-in";
  String CQRS_OUTPUT = "cqrs-out";
  String DOMAIN_INPUT = "domain-in";
  String DOMAIN_OUTPUT = "domain-out";

  @Input(CQRS_INPUT)
  SubscribableChannel cqrsInboundTopic();

  @Output(CQRS_OUTPUT)
  MessageChannel cqrsOutboundTopic();

  @Input(DOMAIN_INPUT)
  SubscribableChannel domainInboundTopic();

  @Output(DOMAIN_OUTPUT)
  MessageChannel domainOutboundTopic();
}
