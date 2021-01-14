package com.fastcampus.vehicle.external;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "operation", url = "${service.endpoint.operation}")
public interface OperationClient {

  @PostMapping("/api/operation")
  Map save(@RequestBody Map addCommand);

  @GetMapping("/api/operation/{id}")
  Map get(@PathVariable("id") String id);
}
