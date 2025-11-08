package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;

public interface UserVehicleAssignmentService {

    UserVehicleAssignment findOrCreateByUserIdAndLicensePlate(Long userId, String licensePlate);

}
