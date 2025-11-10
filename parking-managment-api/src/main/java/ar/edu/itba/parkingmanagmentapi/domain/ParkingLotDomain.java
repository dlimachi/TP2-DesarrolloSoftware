package ar.edu.itba.parkingmanagmentapi.domain;

import java.util.List;

import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ParkingLotDomain {

  private Long id;
  private final String name;
  private final String address;
  private final String imageUrl;
  private final Double latitude;
  private final Double longitude;
  // TODO: should be ManagerDomain!!!
  private Manager manager;
  // TODO: should be SpotDomain!!!
  private List<Spot> spots = List.of();


  public ParkingLot toEntity() {
    ParkingLot parkingLot = new ParkingLot();
    parkingLot.setName(name);
    parkingLot.setAddress(address);
    parkingLot.setImageUrl(imageUrl);
    parkingLot.setLatitude(latitude);
    parkingLot.setLongitude(longitude);
    // TODO: should map to entity
    parkingLot.setManager(manager);
    parkingLot.setSpots(spots);
    return parkingLot;
  }

  public static ParkingLotDomain fromEntity(ParkingLot entity) {
    ParkingLotDomain domain = new ParkingLotDomain(
        entity.getId(),
        entity.getName(),
        entity.getAddress(),
        entity.getImageUrl(),
        entity.getLatitude(),
        entity.getLongitude(),
        // TODO: should map to domain
        entity.getManager(),
        entity.getSpots()
    );
    return domain;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setManager(Manager manager) {
    this.manager = manager;
  }

  public void setSpots(List<Spot> spots) {
    this.spots = spots;
  }
}
