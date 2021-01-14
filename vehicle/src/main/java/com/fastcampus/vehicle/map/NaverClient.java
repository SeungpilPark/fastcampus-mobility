package com.fastcampus.vehicle.map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "naver", url = "${naver.url}", configuration = NaverClientConfig.class)
public interface NaverClient {

  @GetMapping("/map-direction/v1/driving")
  String getRoute(
      @RequestParam("start") String start,
      @RequestParam("waypoints") String waypoints,
      @RequestParam("goal") String goal);
}
