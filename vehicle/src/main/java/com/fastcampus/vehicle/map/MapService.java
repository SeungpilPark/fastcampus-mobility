package com.fastcampus.vehicle.map;

import static com.fastcampus.common.util.ObjectUtils.createObjectMapper;

import com.fastcampus.common.event.vehicle.위치업데이트됨;
import com.fastcampus.vehicle.domain.VehicleRouteEntity;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MapService {

  private final NaverClient naverClient;

  @Autowired
  public MapService(final NaverClient naverClient) {
    this.naverClient = naverClient;
  }

  //경도, 위도
  public VehicleRouteEntity getRoute(
      String originCoordinates, String passengerCoordinates, String destinationCoordinates) {

    try {
      String route = naverClient.getRoute(
          originCoordinates, passengerCoordinates, destinationCoordinates);
      List paths = JsonPath.read(route, "$.route.traoptimal[0].path");
      int passengerPathIndex = JsonPath
          .read(route, "$.route.traoptimal[0].summary.waypoints[0].pointIndex");
      return VehicleRouteEntity.builder()
          .originCoordinates(originCoordinates)
          .passengerCoordinates(passengerCoordinates)
          .destinationCoordinates(destinationCoordinates)
          .paths(createObjectMapper().writeValueAsString(paths))
          .passengerPathIndex(passengerPathIndex)
          .build();
    } catch (IOException ex) {
      throw new RuntimeException("네이버 길찾기 실패", ex);
    }
  }

  public 위치업데이트됨 getCurrentVehicleCoordinates(
      VehicleRouteEntity vehicleRouteEntity, Long elapsedTimeSeconds) {

    double kmPerHour = 100;
    double kmPerSecond = kmPerHour / (60 * 60);
    double kmEstimated = kmPerSecond * elapsedTimeSeconds; // 예상 주행 KM

    try {
      위치업데이트됨 event = new 위치업데이트됨();
      event.setOriginPassed(true);

      List paths = createObjectMapper().readValue(vehicleRouteEntity.getPaths(), List.class);
      int passengerPathIndex = vehicleRouteEntity.getPassengerPathIndex();
      AtomicInteger currentPathIndex = new AtomicInteger(0);
      double cumulativeDistance = 0;
      while (true) {
        if (currentPathIndex.get() == passengerPathIndex) {
          event.setPassengerPassed(true);
        }

        List currentCoordinates = (List) paths.get(currentPathIndex.get());
        double lon1 = (double) currentCoordinates.get(0);
        double lat1 = (double) currentCoordinates.get(1);
        if (currentPathIndex.get() == (paths.size() - 1)) {
          event.setCoordinates(lon1 + "," + lat1);
          event.setDestinationPassed(true);
          break;
        }
        List nextCoordinates = (List) paths.get(currentPathIndex.get() + 1);
        double lon2 = (double) nextCoordinates.get(0);
        double lat2 = (double) nextCoordinates.get(1);

        double distanceOfPath = this.getKmUnitDistance(lon1, lat1, lon2, lat2);
        double nextCumulativeDistance = cumulativeDistance + distanceOfPath;

        if (cumulativeDistance <= kmEstimated && kmEstimated < nextCumulativeDistance) {
          double remainKm = kmEstimated - cumulativeDistance;
          double remainRate = remainKm / distanceOfPath;
          event.setCoordinates(
              this.format(lon1 + ((lon2 - lon1) * remainRate)) + "," +
                  this.format(lat1 + ((lat2 - lat1) * remainRate))
          );
          break;
        }
        cumulativeDistance = cumulativeDistance + distanceOfPath;
        currentPathIndex.incrementAndGet();
      }
      return event;
    } catch (IOException ex) {
      throw new RuntimeException("차량 좌표 계산 실패");
    }
  }

  private double format(double number) {
    return Double.parseDouble(String.format("%.5f", number));
  }

  private double getKmUnitDistance(double lon1, double lat1, double lon2, double lat2) {
    if ((lat1 == lat2) && (lon1 == lon2)) {
      return 0;
    } else {
      double theta = lon1 - lon2;
      double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
          + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math
          .cos(Math.toRadians(theta));
      dist = Math.acos(dist);
      dist = Math.toDegrees(dist);
      return dist * 60 * 1.1515 * 1.609344;
    }
  }
}
