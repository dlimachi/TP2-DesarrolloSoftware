package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DomainSpotRepository {

    boolean existsByParkingLotAndFloorAndCode(ParkingLotDomain parkingLotDomain, int floor, String code);

    boolean existsByParkingLotAndFloorAndCodeAndIdNot(
            ParkingLotDomain parkingLotDomain,
            Integer floor,
            String code,
            Long id
    );

    SpotDomain save(SpotDomain spotDomain);
    Optional<SpotDomain> findById(Long id);
    void delete(SpotDomain spotDomain,ParkingLotDomain parkingLotDomain);
    void deleteById(Long id);
    List<SpotDomain> findAll(Long parkingLotId);
    Page<SpotDomain> findAll(Long parkingLotId, Boolean available, VehicleType vehicleType, Integer floor, Boolean isAccessible, Boolean isReservable, Pageable pageable);
}
