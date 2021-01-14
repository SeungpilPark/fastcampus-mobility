package com.fastcampus.vehicle.external

import com.fastcampus.vehicle.IntegrationTestSupport
import org.springframework.beans.factory.annotation.Autowired

class OperationClientSpec extends IntegrationTestSupport {

    @Autowired
    OperationClient operationClient

    def "operation 서비스 연동 테스트"() {
        given:
        def body = new HashMap<>()
        body.put("passengerCoordinates", "127.1296048,37.3807975")
        body.put("destinationCoordinates", "127.1166015,37.3815819")
        def saved = operationClient.save(body)

        when:
        def response = operationClient.get(saved.get("id").toString())
        then:
        response.get("passengerCoordinates").toString() == "127.1296048,37.3807975"
    }
}
