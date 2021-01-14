package com.fastcampus.vehicle

import com.fastcampus.vehicle.config.ServerConfig
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("default")
@AutoConfigureMockMvc
@ComponentScan(basePackages = ["com.fastcampus"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [ServerConfig.class])
abstract class IntegrationTestSupport extends Specification {
}
