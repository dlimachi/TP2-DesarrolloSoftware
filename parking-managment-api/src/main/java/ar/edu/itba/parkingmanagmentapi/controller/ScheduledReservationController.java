package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.ApiResponse;
import ar.edu.itba.parkingmanagmentapi.dto.PageResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ReservationResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ScheduledReservationRequest;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.mapper.web.ScheduledReservationMapper;
import ar.edu.itba.parkingmanagmentapi.mapper.web.WalkInStayMapper;
import ar.edu.itba.parkingmanagmentapi.service.orchestrator.ReservationOrchestratorService;
import ar.edu.itba.parkingmanagmentapi.validators.ScheduledReservationRequestValidator;
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
    private final ScheduledReservationMapper scheduledReservationMapper;
    private final ScheduledReservationRequestValidator scheduledReservationRequestValidator;

    public ScheduledReservationController(ReservationOrchestratorService reservationOrchestratorService, ScheduledReservationMapper scheduledReservationMapper, ScheduledReservationRequestValidator scheduledReservationRequestValidator) {
        this.reservationOrchestratorService = reservationOrchestratorService;
        this.scheduledReservationMapper = scheduledReservationMapper;
        this.scheduledReservationRequestValidator = scheduledReservationRequestValidator;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ScheduledReservationRequest request) {
        scheduledReservationRequestValidator.validate(request);

        Reservation createdReservation = reservationOrchestratorService.createScheduledReservation(scheduledReservationMapper.toDomain(request));

        return ApiResponse.created(ReservationResponse.fromDomain(createdReservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservation(@PathVariable Long id) {
        Reservation response = reservationOrchestratorService.getScheduledReservationById(id);
        return ApiResponse.ok(scheduledReservationMapper.toDTO(response));
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

        Page<Reservation> reservations = reservationOrchestratorService.getScheduledReservations(criteria, pageable);
        return ApiResponse.ok(reservations.map(scheduledReservationMapper::toDTO));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@authorizationService.canCurrentUserUpdateScheduledReservation(#id)")
    public ResponseEntity<?> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status
    ) {
        Reservation response = reservationOrchestratorService.updateScheduledReservationStatus(id, status);
        return ApiResponse.ok(scheduledReservationMapper.toDTO(response));
    }

}
