package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.dto.*;
import ar.edu.itba.parkingmanagmentapi.exceptions.AuthenticationFailedException;
import ar.edu.itba.parkingmanagmentapi.security.service.SecurityService;
import ar.edu.itba.parkingmanagmentapi.service.orchestrator.UserOrchestratorService;
import ar.edu.itba.parkingmanagmentapi.util.UserMapper;
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
    private final UserOrchestratorService userOrchestratorService;

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isCurrentUser(#id)")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest userDetails) {
        UserResponse updatedUser = userOrchestratorService.updateUser(id, userDetails);
        return ApiResponse.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserResponse user = userOrchestratorService.getUserById(id);
        return ApiResponse.ok(user);
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
        List<VehicleResponse> response = userOrchestratorService.findAllVehiclesByUser(id);
        return ApiResponse.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        //FIXME: The UserResponse mapping should be done at service layer.
        UserResponse currentUser = securityService.getCurrentUser()
                .map(UserMapper::toUserResponse)
                .orElseThrow(() -> new AuthenticationFailedException("No authenticated user found"));
        return ApiResponse.ok(currentUser);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        UserResponse user = userOrchestratorService.findByEmail(email);
        return ApiResponse.ok(user);
    }

}
