package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO: why not at record?
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotRequest {
  private String name;
  private String address;
  private String imageUrl;
  // TODO: serán agregadas por API?
  private Double latitude;
  private Double longitude;

  public ParkingLotDomain toDomain() {
    return new ParkingLotDomain(name, address, imageUrl, latitude, longitude);
  }

  public ParkingLotDomain toDomainWithId(Long id) {
    var parkingLot = toDomain();
    parkingLot.setId(id);
    return parkingLot;
  }
}
