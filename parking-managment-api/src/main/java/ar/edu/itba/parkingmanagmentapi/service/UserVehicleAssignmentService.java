package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;

public interface UserVehicleAssignmentService {

    UserVehicleAssignment findByUserIdAndLicensePlate(Long userId, String licensePlate);

    UserVehicleAssignment createUserAssigment(Long userId, Vehicle vehicle);
}
