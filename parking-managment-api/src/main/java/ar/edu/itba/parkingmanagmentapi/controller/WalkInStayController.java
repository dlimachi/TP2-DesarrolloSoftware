package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.ApiResponse;
import ar.edu.itba.parkingmanagmentapi.dto.PageResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ReservationResponse;
import ar.edu.itba.parkingmanagmentapi.dto.WalkInStayRequest;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.service.orchestrator.ReservationOrchestratorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reservations/walk-in")
@CrossOrigin(origins = "*")
public class WalkInStayController {

    private final ReservationOrchestratorService reservationOrchestratorService;

    public WalkInStayController(ReservationOrchestratorService reservationOrchestratorService) {
        this.reservationOrchestratorService = reservationOrchestratorService;
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfSpot(#request.spotId)")
    public ResponseEntity<?> createWalkInStay(@Valid @RequestBody WalkInStayRequest request) {
        ReservationResponse response = reservationOrchestratorService.createWalkInStayReservation(request);
        return ApiResponse.created(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWalkInStay(@PathVariable Long id) {
        ReservationResponse response = reservationOrchestratorService.getWalkInStayReservationById(id);
        return ApiResponse.ok(response);
    }

    @GetMapping
    @PreAuthorize("@authorizationService.isCurrentUser(#userId)")
    public ResponseEntity<?> getWalkInStaysByUser(
            Long userId,
            @RequestParam(required = false, defaultValue = "ACTIVE") ReservationStatus status,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to,
            Pageable pageable) {
        ReservationCriteria criteria = ReservationCriteria.builder()
                .status(status)
                .userId(userId)
                .licensePlate(licensePlate)
                .range(DateTimeRange.from(from, to))
                .build();

        return ApiResponse.ok(PageResponse.of(reservationOrchestratorService.getWalkInStayReservations(criteria, pageable)));
    }

    @GetMapping("/parking-lot/{parkingLotId}")
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfParkingLot(#parkingLotId)")
    public ResponseEntity<?> getWalkInStaysByParkingLot(
            @PathVariable Long parkingLotId,
            @RequestParam(required = false, defaultValue = "ACTIVE") ReservationStatus status,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to
    ) {
        ReservationCriteria reservationCriteria = ReservationCriteria.builder()
                .status(status)
                .parkingLotId(parkingLotId)
                .licensePlate(licensePlate)
                .range(DateTimeRange.from(from, to))
                .build();

        Page<ReservationResponse> response = reservationOrchestratorService.getWalkInStayReservations(reservationCriteria, Pageable.unpaged());
        return ApiResponse.ok(response.getContent());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@authorizationService.isCurrentUserManagerOfReservation(#id)")
    public ResponseEntity<?> updateWalkInStayStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status
    ) {
        ReservationResponse response = reservationOrchestratorService.updateWalkInReservationStatus(id, status);
        return ApiResponse.ok(response);
    }

    @PatchMapping("/{id}/extend")
    @PreAuthorize("@authorizationService.canCurrentUserUpdateWalkInStayReservation(#id)")
    public ResponseEntity<?> extend(
            @PathVariable Long id,
            @RequestParam int extraHours) {
        return ApiResponse.ok(reservationOrchestratorService.extendWalkInReservation(id, extraHours));
    }

    @GetMapping("/{id}/remaining-time")
    public ResponseEntity<?> remainingTime(@PathVariable Long id) {
        Duration remaining = reservationOrchestratorService.getRemainingTime(id);
        return ApiResponse.ok("Remaining: " + remaining.toMinutes() + " minutes");
    }
}
