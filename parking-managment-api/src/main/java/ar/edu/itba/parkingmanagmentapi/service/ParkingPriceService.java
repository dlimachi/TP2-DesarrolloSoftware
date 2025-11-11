package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.ParkingPriceDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.util.ParkingPriceFilter;

import java.math.BigDecimal;
import java.util.List;

public interface ParkingPriceService {

    ParkingPriceDomain create(Long parkingLotId, ParkingPriceDomain domain);

    ParkingPriceDomain update(Long parkingLotId, Long id, ParkingPriceDomain domain);

    ParkingPriceDomain getById(Long parkingLotId, Long id);

    BigDecimal calculateEstimatedPrice(Long parkingLotId, DateTimeRange range);

    BigDecimal calculateEstimatedPrice(Long parkingLotId, VehicleType vehicleType, DateTimeRange range);

    ParkingPriceDomain findActivePriceBySpotIdAndVehicleType(Long parkingLotId, VehicleType vehicleType);

    List<ParkingPriceDomain> getByParkingLot(Long parkingLotId);

    void delete(Long parkingLotId, Long id);

    List<ParkingPriceDomain> getByFilters(Long parkingLotId, ParkingPriceFilter filter);

    boolean existsActiveByParkingLotIdAndVehicleType(Long parkingLotId, VehicleType vehicleType);
}
