package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface DomainSpotRepository {

    boolean existsByParkingLotAndFloorAndCode(ParkingLot parkingLotDomain, int floor, String code);

    boolean existsByParkingLotAndFloorAndCodeAndIdNot(
            ParkingLot parkingLotDomain,
            Integer floor,
            String code,
            Long id
    );

    SpotDomain save(SpotDomain spotDomain);
    Optional<SpotDomain> findById(Long id);
    void delete(SpotDomain spotDomain);
    Page<SpotDomain> findAll(Long parkingLotId, Boolean available, VehicleType vehicleType, Integer floor, Boolean isAccessible, Boolean isReservable, Pageable pageable);
}
