package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.model.ParkingPrice;
import java.util.List;

public interface DomainParkingPriceRepository {

    ParkingPrice save(ParkingPrice parkingPrice);
    ParkingPrice findById(Long id);
    List<ParkingPrice> findByParkingLotIdAndVehicleType(Long parkingLotId, VehicleType type);
    boolean existsActivePrice(Long parkingLotId, VehicleType type);
    void delete(ParkingPrice parkingPrice);
}
