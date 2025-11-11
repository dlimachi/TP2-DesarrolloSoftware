package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import java.util.List;

public interface VehicleService {

    VehicleDomain create(VehicleDomain request, UserVehicleAssignment vehicleAssignment);

    VehicleDomain  findByLicensePlate(String licensePlate);

    List<VehicleDomain> findAllVehiclesByUser(Long id);

    VehicleDomain update(VehicleDomain request);

    void delete(String licensePlate);

    boolean isUserOwnerOfVehicle(Long userId, String licensePlate);

    // -------------------------- RAW ENTITIES --------------------------


}
