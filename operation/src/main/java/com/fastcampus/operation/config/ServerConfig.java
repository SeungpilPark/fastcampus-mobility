package com.fastcampus.operation.config;

import com.fastcampus.operation.kafka.KafkaStreams;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableJpaAuditing
@EnableBinding(KafkaStreams.class)
@EnableAsync
public class ServerConfig {

}
