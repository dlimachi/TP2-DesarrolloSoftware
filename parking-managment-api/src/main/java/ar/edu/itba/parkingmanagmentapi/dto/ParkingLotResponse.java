package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.model.Spot;

@Getter
@Setter
@RequiredArgsConstructor
public class ParkingLotResponse {
  private final Long id;
  private final String name;
  private final String address;
  private final String imageUrl;
  private List<SpotResponse> spots;

  public static ParkingLotResponse fromDomain(ParkingLotDomain parkingLot) {
    ParkingLotResponse response = new ParkingLotResponse(
        parkingLot.getId(),
        parkingLot.getName(),
        parkingLot.getAddress(),
        parkingLot.getImageUrl());

    response.setSpots(parkingLot.getSpots().stream()
        .map(ParkingLotResponse::toSpotResponse)
        .collect(Collectors.toList()));

    return response;
  }

  // TODO: should be in SpotResponse or something. And should use SpotDomain
  private static SpotResponse toSpotResponse(SpotDomain spot) {
    SpotResponse dto = new SpotResponse();
    dto.setId(spot.getId());
    dto.setVehicleType(spot.getVehicleType().getName());
    dto.setFloor(spot.getFloor());
    dto.setCode(spot.getCode());
    dto.setIsAvailable(spot.getIsAvailable());
    dto.setIsReservable(spot.getIsReservable());
    dto.setIsAccessible(spot.getIsAccessible());
    return dto;
  }
}
