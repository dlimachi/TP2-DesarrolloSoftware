package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.dto.*;
import ar.edu.itba.parkingmanagmentapi.exceptions.AuthenticationFailedException;
import ar.edu.itba.parkingmanagmentapi.security.service.SecurityService;
import ar.edu.itba.parkingmanagmentapi.service.orchestrator.UserOrchestratorService;
import ar.edu.itba.parkingmanagmentapi.util.UserMapper;
import ar.edu.itba.parkingmanagmentapi.validators.CreateUserRequestValidator;
import ar.edu.itba.parkingmanagmentapi.validators.UpdatedUserRequestedValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final SecurityService securityService;
    // private final CreateUserRequestValidator createUserRequestValidator;
    private final UpdatedUserRequestedValidator updatedUserRequestValidator;
    private final UserOrchestratorService userOrchestratorService;

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest userDetails) {
        updatedUserRequestValidator.validate(userDetails);
        UserDomain user = userOrchestratorService.updateUser(userDetails.toDomain());
        return ApiResponse.ok(UserResponse.fromDomain(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserDomain user = userOrchestratorService.getUserById(id);
        return ApiResponse.ok(UserResponse.fromDomain(user));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.isCurrentUser(#id)")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userOrchestratorService.deleteUser(id);
        return ApiResponse.noContent();
    }

    // -------------------------- EXTENSIONS --------------------------

    @GetMapping("/{id}/vehicles")
    public ResponseEntity<?> getAllVehicles(@PathVariable Long id) {
        List<VehicleResponse> response = userOrchestratorService.findAllVehiclesByUser(id).stream()
                .map(VehicleResponse::fromDomain)
                .toList();
        return ApiResponse.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        // FIXME: The UserResponse mapping should be done at service layer.
        UserResponse currentUser = securityService.getCurrentUser()
                .map(UserResponse::fromDomain)
                .orElseThrow(() -> new AuthenticationFailedException("No authenticated user found"));
        return ApiResponse.ok(currentUser);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        UserDomain user = userOrchestratorService.findByEmail(email);
        return ApiResponse.ok(UserResponse.fromDomain(user));
    }

}
