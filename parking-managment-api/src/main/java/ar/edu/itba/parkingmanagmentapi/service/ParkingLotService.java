package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;

import java.util.List;
import java.util.Optional;

public interface ParkingLotService {

    /**
     * Creates a new parking lot
     */
    ParkingLotDomain createParkingLot(ParkingLotDomain parkingLotRequest);

    /**
     * Updates an existing parking lot
     */
    ParkingLotDomain updateParkingLot(ParkingLotDomain parkingLotRequest);

    /**
     * Finds a parking lot by ID
     */
    ParkingLotDomain findById(Long id);

    /**
     * Lists all parking lots
     */
    List<ParkingLotDomain> findAll();

    /**
     * Deletes a parking lot by ID
     */
    void deleteParkingLot(Long id);

    // TODO: remove
    ParkingLot findEntityById(Long id);

    /**
     * Gets the manager (User) of a parking lot by its ID
     */
    Optional<UserDomain> getManagerOfParkingLot(Long parkingLotId);

    /**
     * Finds all parking lots owned by a specific user ID
     */
    List<ParkingLotDomain> findByUserId(Long userId);

}
