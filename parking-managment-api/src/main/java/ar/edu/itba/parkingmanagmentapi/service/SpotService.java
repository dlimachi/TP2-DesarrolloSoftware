package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;

import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SpotService {
    SpotDomain createSpot(Long parkingLotId , SpotDomain spotDomain);

    SpotDomain findById(Long parkingLotId, Long id);

    SpotDomain updateSpot(Long parkingLotId, Long id, SpotDomain spotDomain);

    void deleteSpot(Long parkingLotId, Long id);

    //TODO: Chango to use UserDomain!!
    Optional<User> getManagerOfSpot(Long spotId);

    Page<SpotDomain> findByFilters(Long parkingLotId, Boolean available, VehicleType vehicleType, Integer floor, Boolean isAccessible, Boolean isReservable, Pageable pageable);

    // -------------------------- RAW ENTITIES --------------------------

    SpotDomain findEntityById(Long spotId);

    SpotDomain updateEntity(SpotDomain spotDomain);

    boolean toggleAvailability(Long spotId);

    boolean isAvailable(Long spotId);

}