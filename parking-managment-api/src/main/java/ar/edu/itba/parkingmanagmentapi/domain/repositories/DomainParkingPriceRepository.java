package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingPriceDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.model.ParkingPrice;
import ar.edu.itba.parkingmanagmentapi.util.ParkingPriceFilter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface DomainParkingPriceRepository {

    ParkingPriceDomain save(ParkingPriceDomain parkingPrice);
    ParkingPriceDomain update(ParkingPriceDomain domain);
    void deleteById(Long id);
    ParkingPriceDomain findById(Long id);
    List<ParkingPriceDomain> findByParkingLotIdAndVehicleType(Long parkingLotId, VehicleType type);
    boolean existsActivePrice(Long parkingLotId, VehicleType type);
    List<ParkingPriceDomain> findByFilters(Long parkingLotId, ParkingPriceFilter filter);
}
