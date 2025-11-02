package ar.edu.itba.parkingmanagmentapi.service.orchestrator;

import ar.edu.itba.parkingmanagmentapi.dto.UserResponse;
import ar.edu.itba.parkingmanagmentapi.dto.VehicleResponse;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.service.UserService;
import ar.edu.itba.parkingmanagmentapi.service.VehicleService;
import ar.edu.itba.parkingmanagmentapi.util.UserMapper;
import ar.edu.itba.parkingmanagmentapi.validators.CreateUserRequestValidator;
import ar.edu.itba.parkingmanagmentapi.validators.UpdatedUserRequestedValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserOrchestratorService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CreateUserRequestValidator createUserRequestValidator;
    private final UpdatedUserRequestedValidator updatedUserRequestValidator;
    private final VehicleService vehicleService;

    public UserResponse createUser(ar.edu.itba.parkingmanagmentapi.dto.CreateUserRequest userRequest) {
        createUserRequestValidator.validate(userRequest);
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        return userService.createUser(userRequest, encodedPassword);
    }

    public UserResponse updateUser(Long id, ar.edu.itba.parkingmanagmentapi.dto.UpdateUserRequest userRequest) {
        updatedUserRequestValidator.validate(userRequest);
        return userService.updateUser(id, userRequest);
    }

    public UserResponse findByEmail(String email) {
        return userService.findByEmail(email);
    }

    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

    public List<VehicleResponse> findAllVehiclesByUser(Long userId) {
        return vehicleService.findAllVehiclesByUser(userId);
    }

    public UserResponse getUserById(Long id) {
        User user = userService.findById(id);
        return UserMapper.toUserResponse(user);
    }
}
