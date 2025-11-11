package ar.edu.itba.parkingmanagmentapi.domain;

import java.util.List;

import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
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
  private List<SpotDomain> spots = List.of();


  public ParkingLot toEntity() {
    ParkingLot parkingLot = new ParkingLot();
      if (id != null)
          parkingLot.setId(id);
    parkingLot.setName(name);
    parkingLot.setAddress(address);
    parkingLot.setImageUrl(imageUrl);
    parkingLot.setLatitude(latitude);
    parkingLot.setLongitude(longitude);
    // TODO: should map to entity
    parkingLot.setManager(manager);
    // Spots nos assigned here
    return parkingLot;
  }

  public static ParkingLotDomain fromEntity(ParkingLot entity) {

      List<SpotDomain> spotDomains = entity.getSpots().stream().map(SpotDomain::fromEntity).toList();

      ParkingLotDomain domain = new ParkingLotDomain(
        entity.getId(),
        entity.getName(),
        entity.getAddress(),
        entity.getImageUrl(),
        entity.getLatitude(),
        entity.getLongitude(),
        // TODO: should map to domain
        entity.getManager(),
        spotDomains
    );
    return domain;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setManager(Manager manager) {
    this.manager = manager;
  }

  public void setSpots(List<SpotDomain> spots) {
    this.spots = spots;
  }
}
