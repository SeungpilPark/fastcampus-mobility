package com.fastcampus.vehicle.config;

import static com.fastcampus.common.util.ObjectUtils.createObjectMapper;

import feign.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@EnableFeignClients(basePackages = "com.fastcampus")
public class FeignClientConfig {

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public SpringEncoder getSpringEncoder() {
    return new SpringEncoder(getObjectFactory());
  }

  public ObjectFactory<HttpMessageConverters> getObjectFactory() {
    return () -> new HttpMessageConverters(
        new MappingJackson2HttpMessageConverter(createObjectMapper())
    );
  }
}
