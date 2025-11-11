package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingPriceDomain;
import ar.edu.itba.parkingmanagmentapi.dto.ApiResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ParkingPriceRequest;
import ar.edu.itba.parkingmanagmentapi.dto.ParkingPriceResponse;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.service.ParkingPriceService;
import ar.edu.itba.parkingmanagmentapi.util.ParkingPriceFilter;
import ar.edu.itba.parkingmanagmentapi.validators.ParkingPriceRequestValidator;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/parking-lots/{parkingLotId}/prices")
@CrossOrigin(origins = "*")
public class ParkingPriceController {

    private final ParkingPriceService parkingPriceService;
    private final ParkingPriceRequestValidator validator;

    public ParkingPriceController(ParkingPriceService parkingPriceService, ParkingPriceRequestValidator validator) {
        this.parkingPriceService = parkingPriceService;
        this.validator = validator;
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfParkingLot(#parkingLotId)")
    public ResponseEntity<?> create(
            @PathVariable Long parkingLotId,
            @Valid @RequestBody ParkingPriceRequest request
    ) {
        validator.validate(request);
        ParkingPriceDomain domain = ParkingPriceRequest.toDomain(request);
        ParkingPriceDomain created = parkingPriceService.create(parkingLotId, domain);
        ParkingPriceResponse response = ParkingPriceResponse.fromDomain(created);
        return ApiResponse.created(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfParkingLot(#parkingLotId)")
    public ResponseEntity<?> update(
            @PathVariable Long parkingLotId,
            @PathVariable Long id,
            @Valid @RequestBody ParkingPriceRequest request
    ) {
        validator.validate(request);
        ParkingPriceDomain domain = ParkingPriceRequest.toDomain(request);
        ParkingPriceDomain updated = parkingPriceService.update(parkingLotId, id, domain);
        ParkingPriceResponse response = ParkingPriceResponse.fromDomain(updated);
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfParkingLot(#parkingLotId)")
    public ResponseEntity<?> delete(
            @PathVariable Long parkingLotId,
            @PathVariable Long id
    ) {
        parkingPriceService.delete(parkingLotId, id);
        return ApiResponse.noContent();
    }

    @GetMapping
    public ResponseEntity<?> searchPrices(
            @PathVariable Long parkingLotId,
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        VehicleType vehicleTypeEnum = vehicleType != null && !vehicleType.isBlank()
                ? VehicleType.fromName(vehicleType)
                : null;

        ParkingPriceFilter filter = new ParkingPriceFilter(min, max, vehicleTypeEnum, from, to, sort);

        List<ParkingPriceDomain> domains = parkingPriceService.getByFilters(parkingLotId, filter);
        List<ParkingPriceResponse> responses = domains.stream()
                .map(ParkingPriceResponse::fromDomain)
                .toList();

        return ApiResponse.ok(responses);
    }
}
