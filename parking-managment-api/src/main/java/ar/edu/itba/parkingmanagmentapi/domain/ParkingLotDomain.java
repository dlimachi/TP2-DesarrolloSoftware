package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;

public record ParkingLotDomain(String name, String address, String imageUrl, Double latitude, Double longitude) {
  public ParkingLot toPersistence(Manager currentManager) {
    ParkingLot parkingLot = new ParkingLot();
    parkingLot.setName(name);
    parkingLot.setAddress(address);
    parkingLot.setImageUrl(imageUrl);
    parkingLot.setLatitude(latitude);
    parkingLot.setLongitude(longitude);
    parkingLot.setManager(currentManager);
    return parkingLot;
  }
}
