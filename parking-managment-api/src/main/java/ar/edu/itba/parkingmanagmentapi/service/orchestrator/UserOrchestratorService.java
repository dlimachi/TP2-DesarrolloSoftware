package ar.edu.itba.parkingmanagmentapi.service.orchestrator;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import ar.edu.itba.parkingmanagmentapi.service.UserService;
import ar.edu.itba.parkingmanagmentapi.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserOrchestratorService {
    private final UserService userService;
    private final VehicleService vehicleService;

    public UserDomain updateUser(UserDomain user) {
        return userService.updateUser(user);
    }

    public UserDomain findByEmail(String email) {
        return userService.findByEmail(email);
    }

    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

    public List<VehicleDomain> findAllVehiclesByUser(Long userId) {
        return vehicleService.findAllVehiclesByUser(userId);
    }

    public UserDomain getUserById(Long id) {
        return userService.findById(id);
    }
}
