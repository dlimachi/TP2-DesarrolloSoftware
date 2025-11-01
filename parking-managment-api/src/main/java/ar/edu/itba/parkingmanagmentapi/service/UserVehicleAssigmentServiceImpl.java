package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;
import ar.edu.itba.parkingmanagmentapi.repository.UserRepository;
import ar.edu.itba.parkingmanagmentapi.repository.UserVehicleAssignmentRepository;
import ar.edu.itba.parkingmanagmentapi.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserVehicleAssigmentServiceImpl implements UserVehicleAssignmentService {

    private final UserVehicleAssignmentRepository userVehicleAssignmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public UserVehicleAssigmentServiceImpl(UserVehicleAssignmentRepository userVehicleAssignmentRepository, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.userVehicleAssignmentRepository = userVehicleAssignmentRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public UserVehicleAssignment findByUserIdAndLicensePlate(Long userId, String licensePlate) {
        return userVehicleAssignmentRepository
                .findByUserIdAndVehicleLicensePlate(userId, licensePlate)
                .orElseThrow(() -> new NotFoundException("This assignment does not exist"));
    }

    @Override
    @Transactional
    public UserVehicleAssignment createUserAssigment(Long userId, Vehicle vehicle) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user.not.found"));
        return userVehicleAssignmentRepository.save(new UserVehicleAssignment(user, vehicle));

    }
}
