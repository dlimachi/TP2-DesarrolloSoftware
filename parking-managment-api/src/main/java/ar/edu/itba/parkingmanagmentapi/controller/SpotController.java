package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import ar.edu.itba.parkingmanagmentapi.dto.ApiResponse;
import ar.edu.itba.parkingmanagmentapi.dto.PageResponse;
import ar.edu.itba.parkingmanagmentapi.dto.SpotRequest;
import ar.edu.itba.parkingmanagmentapi.dto.SpotResponse;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.service.SpotService;
import ar.edu.itba.parkingmanagmentapi.validators.SpotRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parking-lots/{parkingLotId}/spots")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;
    private final SpotRequestValidator spotRequestValidator;

    @PostMapping
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfParkingLot(#parkingLotId)")
    public ResponseEntity<?> createSpot(@PathVariable Long parkingLotId, @RequestBody SpotRequest spotRequest) {
        spotRequestValidator.validate(spotRequest);
        SpotDomain spotDomain = spotRequest.toDomain(spotRequest);
        SpotDomain result = spotService.createSpot(parkingLotId, spotDomain);
        SpotResponse createdSpot = SpotResponse.fromDomain(result);
        return ApiResponse.created(createdSpot);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSpotById(@PathVariable Long parkingLotId, @PathVariable Long id) {
        SpotDomain spotDomain = spotService.findById(parkingLotId, id);
        SpotResponse spotResponse = SpotResponse.fromDomain(spotDomain);
        return ApiResponse.ok(spotResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfSpot(#id)")
    public ResponseEntity<?> updateSpot(@PathVariable Long parkingLotId, @PathVariable Long id, @RequestBody SpotDomain spotDomain) {
        SpotDomain updatedSpotDomain = spotService.updateSpot(parkingLotId, id, spotDomain);
        SpotResponse spotResponse = SpotResponse.fromDomain(updatedSpotDomain);
        return ApiResponse.ok(spotResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfSpot(#id)")
    public ResponseEntity<?> deleteSpot(@PathVariable Long parkingLotId, @PathVariable Long id) {
        spotService.deleteSpot(parkingLotId, id);
        return ApiResponse.noContent();
    }

    @GetMapping
    public ResponseEntity<?> getSpots(
            @PathVariable Long parkingLotId,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Boolean isAccessible,
            @RequestParam(required = false) Boolean isReservable,
            Pageable pageable) {
        VehicleType vehicleTypeEnum = vehicleType != null && !vehicleType.isBlank()
                ? VehicleType.fromName(vehicleType)
                : null;
        Page<SpotDomain> spots = spotService.findByFilters(parkingLotId, available, vehicleTypeEnum, floor, isAccessible, isReservable, pageable);
        Page<SpotResponse> spotResponses = spots.map(SpotResponse::fromDomain);
        return ApiResponse.ok(PageResponse.of(spots));
    }
}

