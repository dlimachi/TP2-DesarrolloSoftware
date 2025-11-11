package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.dto.ApiResponse;
import ar.edu.itba.parkingmanagmentapi.dto.VehicleRequest;
import ar.edu.itba.parkingmanagmentapi.dto.VehicleResponse;
import ar.edu.itba.parkingmanagmentapi.service.orchestrator.AssignVehicleOrchestratorService;
import ar.edu.itba.parkingmanagmentapi.validators.CreateVehicleValidator;
import ar.edu.itba.parkingmanagmentapi.validators.UpdateVehicleValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VehicleController {
    private final AssignVehicleOrchestratorService assignVehicleOrchestratorService;
    private final CreateVehicleValidator createVehicleValidator;
    private final UpdateVehicleValidator updateVehicleValidator;


    @PostMapping

    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleRequest request) {
        createVehicleValidator.validate(request);
        VehicleResponse response = VehicleResponse.fromDomain(assignVehicleOrchestratorService.assignVehicleToUser(request.toDomain()));
        return ApiResponse.created(response);
    }

    @GetMapping("/{licensePlate}")
    @PreAuthorize("@authorizationService.isCurrentUserOwnerOfVehicle(#licensePlate)")
    public ResponseEntity<?> getVehicle(@PathVariable String licensePlate) {
        VehicleResponse response = VehicleResponse.fromDomain(assignVehicleOrchestratorService.findByLicensePlate(licensePlate));
        return ApiResponse.ok(response);
    }

    @PutMapping("/{licensePlate}")
    @PreAuthorize("@authorizationService.isCurrentUserOwnerOfVehicle(#licensePlate)")
    public ResponseEntity<?> updateVehicle(
            @PathVariable String licensePlate,
            @Valid @RequestBody VehicleRequest request) {
        updateVehicleValidator.validate(request);
        VehicleResponse response = VehicleResponse.fromDomain(assignVehicleOrchestratorService.updateVehicleForUser(licensePlate, request.toDomain()));
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/{licensePlate}")
    @PreAuthorize("@authorizationService.isCurrentUserOwnerOfVehicle(#licensePlate)")
    public ResponseEntity<?> deleteVehicle(@PathVariable String licensePlate) {
        assignVehicleOrchestratorService.deleteVehicleForUser(licensePlate);
        return ApiResponse.noContent();
    }
}

