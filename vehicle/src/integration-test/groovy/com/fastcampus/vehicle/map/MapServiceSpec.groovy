package com.fastcampus.vehicle.map

import com.fastcampus.vehicle.IntegrationTestSupport
import org.springframework.beans.factory.annotation.Autowired

class MapServiceSpec extends IntegrationTestSupport {

    @Autowired
    MapService mapService

    def "네이버 길찾기 API 테스트"() {
        given:
        when:
        def routeEntity = mapService.getRoute(
                "127.1302485,37.3752388",
                "127.1296048,37.3807975",
                "127.1166015,37.3815819"
        )
        then:
        routeEntity.paths.length() > 0
    }

    def "차량 좌표 계산 "() {
        given:
        def routeEntity = mapService.getRoute(
                "127.1302485,37.3752388",
                "127.1296048,37.3807975",
                "127.1166015,37.3815819"
        )

        when:
        for (int i = 1; i < 100; i++) {
            def event = mapService.getCurrentVehicleCoordinates(routeEntity, i)
            println(event.toString())
        }
        then:
        1 == 1
    }
}
