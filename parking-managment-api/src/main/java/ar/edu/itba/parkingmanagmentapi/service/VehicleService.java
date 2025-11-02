package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.dto.VehicleResponse;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;
import java.util.List;

public interface VehicleService {

    Vehicle create(Vehicle request);

    Vehicle findByLicensePlate(String licensePlate);

    List<VehicleResponse> findAllVehiclesByUser(Long id);

    Vehicle update(String licensePlate, Vehicle request);

    void delete(String licensePlate);

    boolean isUserOwnerOfVehicle(Long userId, String licensePlate);

    // -------------------------- RAW ENTITIES --------------------------

    Vehicle findEntityByLicensePlateOrCreate(Vehicle vehicle);

}
