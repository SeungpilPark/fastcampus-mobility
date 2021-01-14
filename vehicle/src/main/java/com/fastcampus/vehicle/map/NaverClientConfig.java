package com.fastcampus.vehicle.map;

import feign.RequestInterceptor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
public class NaverClientConfig {

  @Value("${naver.key}")
  private String key;
  @Value("${naver.secret}")
  private String secret;

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      requestTemplate.header("X-NCP-APIGW-API-KEY-ID", key);
      requestTemplate.header("X-NCP-APIGW-API-KEY", secret);
    };
  }
}
