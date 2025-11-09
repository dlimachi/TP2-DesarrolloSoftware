package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;
import ar.edu.itba.parkingmanagmentapi.repository.UserRepository;
import ar.edu.itba.parkingmanagmentapi.repository.UserVehicleAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserVehicleAssigmentServiceImpl implements UserVehicleAssignmentService {

    private final UserVehicleAssignmentRepository userVehicleAssignmentRepository;
    private final UserRepository userRepository;

    public UserVehicleAssigmentServiceImpl(UserVehicleAssignmentRepository userVehicleAssignmentRepository, UserRepository userRepository) {
        this.userVehicleAssignmentRepository = userVehicleAssignmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserVehicleAssignment findOrCreateByUserIdAndLicensePlate(Long userId, String licensePlate) {
        return userVehicleAssignmentRepository
                .findByUserIdAndVehicleLicensePlate(userId, licensePlate)
                .orElse(null);
    }
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("This user does not exist"));
                    Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate).orElseThrow(() -> new NotFoundException("This vehicle does not exist"));

                    return userVehicleAssignmentRepository.save(new UserVehicleAssignment(user, vehicle));
                });
    }
}
