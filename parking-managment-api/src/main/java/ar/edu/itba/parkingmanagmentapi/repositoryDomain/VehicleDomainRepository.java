package ar.edu.itba.parkingmanagmentapi.repositoryDomain;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;

import java.util.List;
import java.util.Optional;

public interface VehicleDomainRepository {
    VehicleDomain save(VehicleDomain vehicle, UserVehicleAssignment vehicleAssignment);
    VehicleDomain findByLicensePlate(String licensePlate);

    List<VehicleDomain> findAllByUserId(Long userId);

    void deleteById(String licensePlate);

    VehicleDomain update(VehicleDomain request);

    boolean findOwnerOfVehicle(Long userId, String licensePlate);
}