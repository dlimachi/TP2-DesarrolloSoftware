package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.ApiResponse;
import ar.edu.itba.parkingmanagmentapi.dto.PageResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ReservationResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ScheduledReservationRequest;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.service.orchestrator.ReservationOrchestratorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/reservations/scheduled")
@CrossOrigin(origins = "*")
public class ScheduledReservationController {

    private final ReservationOrchestratorService reservationOrchestratorService;

    public ScheduledReservationController(ReservationOrchestratorService reservationOrchestratorService) {
        this.reservationOrchestratorService = reservationOrchestratorService;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ScheduledReservationRequest request) {
        ReservationResponse response = reservationOrchestratorService.createScheduledReservation(request);
        return ApiResponse.created(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservation(@PathVariable Long id) {
        ReservationResponse response = reservationOrchestratorService.getScheduledReservationById(id);
        return ApiResponse.ok(response);
    }

    @GetMapping
    @PreAuthorize("@authorizationService.isCurrentUser(#userId)")
    public ResponseEntity<?> getReservationsByUser(
            Long userId,
            @RequestParam(required = false, defaultValue = "PENDING") ReservationStatus status,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to,
            Pageable pageable) {
        ReservationCriteria criteria = ReservationCriteria.builder()
                .userId(userId)
                .licensePlate(licensePlate)
                .status(status)
                .range(DateTimeRange.from(from, to))
                .build();

        Page<ReservationResponse> responses = reservationOrchestratorService.getScheduledReservations(criteria, pageable);
        return ApiResponse.ok(PageResponse.of(responses));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@authorizationService.canCurrentUserUpdateScheduledReservation(#id)")
    public ResponseEntity<?> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status
    ) {
        ReservationResponse response = reservationOrchestratorService.updateScheduledReservationStatus(id, status);
        return ApiResponse.ok(response);
    }

}
