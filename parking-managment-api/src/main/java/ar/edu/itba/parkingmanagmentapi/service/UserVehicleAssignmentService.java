package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;

public interface UserVehicleAssignmentService {

    UserVehicleAssignment create(Long userId, String licensePlate);

    UserVehicleAssignment findOrCreateByUserIdAndLicensePlate(Long userId, String licensePlate);

    UserVehicleAssignment findByUserIdAndLicensePlate(Long userId, String licensePlate);

}
