package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import java.util.List;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;

public interface DomainParkingLotRepository {

  public void deleteById(Long id);
  public List<ParkingLotDomain> findAll();
  public ParkingLotDomain findById(Long id);
  public List<ParkingLotDomain> findByManagerUserId(Long id);
  public ParkingLotDomain save(ParkingLotDomain parkingLot);
  public ParkingLotDomain update(ParkingLotDomain parkingLot);
  
}
