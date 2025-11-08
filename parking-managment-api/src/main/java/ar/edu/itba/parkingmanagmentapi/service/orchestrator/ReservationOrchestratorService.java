package ar.edu.itba.parkingmanagmentapi.service.orchestrator;

import ar.edu.itba.parkingmanagmentapi.config.AppConstants;
import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.ReservationResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ScheduledReservationRequest;
import ar.edu.itba.parkingmanagmentapi.dto.WalkInStayRequest;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.*;
import ar.edu.itba.parkingmanagmentapi.service.*;
import ar.edu.itba.parkingmanagmentapi.validators.ScheduledReservationRequestValidator;
import ar.edu.itba.parkingmanagmentapi.validators.WalkInStayRequestValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationOrchestratorService {

    private final WalkInStayRequestValidator walkInStayRequestValidator;
    private final ScheduledReservationRequestValidator scheduledReservationRequestValidator;

    private final ScheduledReservationService scheduledReservationService;
    private final WalkInStayService walkInStayService;
    private final SpotService spotService;
    private final ParkingPriceService parkingPriceService;
    private final UserVehicleAssignmentService userVehicleAssignmentService;

    public ReservationOrchestratorService(WalkInStayRequestValidator walkInStayRequestValidator, ScheduledReservationRequestValidator scheduledReservationRequestValidator, ScheduledReservationService scheduledReservationService, WalkInStayService walkInStayService, SpotService spotService, ParkingPriceService parkingPriceService, UserVehicleAssignmentService userVehicleAssignmentService) {
        this.walkInStayRequestValidator = walkInStayRequestValidator;
        this.scheduledReservationRequestValidator = scheduledReservationRequestValidator;
        this.scheduledReservationService = scheduledReservationService;
        this.walkInStayService = walkInStayService;
        this.spotService = spotService;
        this.parkingPriceService = parkingPriceService;
        this.userVehicleAssignmentService = userVehicleAssignmentService;
    }

    public ReservationResponse createWalkInStayReservation(final WalkInStayRequest walkInStayRequest) {
        walkInStayRequestValidator.validate(walkInStayRequest);

        Spot spot = spotService.findEntityById(walkInStayRequest.getSpotId());

        if (!parkingPriceService.existsActiveByParkingLotIdAndVehicleType(spot.getParkingLot().getId(), spot.getVehicleType())) {
            throw new NotFoundException("There are no active prices for this type of vehicle in the parking lot");
        }

        UserVehicleAssignment assignment = userVehicleAssignmentService.findOrCreateByUserIdAndLicensePlate(AppConstants.DEFAULT_USER_ID, walkInStayRequest.getVehicleLicensePlate());

        WalkInStay stay = new WalkInStay();
        stay.setCheckInTime(LocalDateTime.now());
        stay.setStatus(ReservationStatus.ACTIVE);
        stay.setExpectedEndTime(stay.getCheckInTime().plusHours(walkInStayRequest.getExpectedDurationHours()));
        stay.setSpot(spot);
        stay.setCheckOutTime(null);
        stay.setUserVehicleAssignment(assignment);

        spotService.toggleAvailability(spot.getId());
        WalkInStay savedWalkInStay = walkInStayService.createReservation(stay);

        return ReservationResponse.fromWalkInStay(savedWalkInStay);
    }

    public ReservationResponse createScheduledReservation(final ScheduledReservationRequest scheduledReservation) {
        scheduledReservationRequestValidator.validate(scheduledReservation);

        Spot spot = spotService.findEntityById(scheduledReservation.getSpotId());

        boolean isAvailable = spotService.isAvailable(spot.getId());
        if (!isAvailable) {
            throw new BadRequestException("spot.not.available", spot.getId());
        }

        List<ScheduledReservation> overlapping = scheduledReservationService.findBySpotIdAndOverlappingPeriod(
                spot.getId(),
                DateTimeRange.from(scheduledReservation.getReservedStartTime(), scheduledReservation.getExpectedEndTime())
        );

        if (!overlapping.isEmpty()) {
            throw new BadRequestException("spot.not.available", spot.getId());
        }

        BigDecimal estimatedPrice = parkingPriceService.calculateEstimatedPrice(spot.getParkingLot().getId(), spot.getVehicleType(), DateTimeRange.from(scheduledReservation.getReservedStartTime(), scheduledReservation.getExpectedEndTime()));

        UserVehicleAssignment assignment = userVehicleAssignmentService.findOrCreateByUserIdAndLicensePlate(scheduledReservation.getUserId(), scheduledReservation.getVehicleLicensePlate());

        LocalDateTime now = LocalDateTime.now();
        ScheduledReservation reservation = new ScheduledReservation();
        reservation.setReservedStartTime(scheduledReservation.getReservedStartTime());
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now);
        reservation.setExpectedEndTime(scheduledReservation.getExpectedEndTime());
        reservation.setEstimatedPrice(estimatedPrice);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setSpot(spot);
        reservation.setUserVehicleAssignment(assignment);

        spotService.toggleAvailability(spot.getId());
        scheduledReservationService.create(reservation);

        return ReservationResponse.fromScheduledReservation(reservation);
    }

    public ReservationResponse getScheduledReservationById(final Long reservationId) {
        return ReservationResponse.fromScheduledReservation(scheduledReservationService.findById(reservationId));
    }

    public ReservationResponse getWalkInStayReservationById(final Long reservationId) {
        return ReservationResponse.fromWalkInStay(walkInStayService.findById(reservationId));
    }

    public Page<ReservationResponse> getScheduledReservations(final ReservationCriteria reservationCriteria, Pageable pageable) {
        return scheduledReservationService.findByCriteria(reservationCriteria, pageable)
                .map(ReservationResponse::fromScheduledReservation);
    }

    public Page<ReservationResponse> getWalkInStayReservations(final ReservationCriteria reservationCriteria, Pageable pageable) {
        return walkInStayService.findByCriteria(reservationCriteria, pageable)
                .map(ReservationResponse::fromWalkInStay);
    }

    public ReservationResponse updateScheduledReservationStatus(Long reservationId, ReservationStatus status) {
        return ReservationResponse.fromScheduledReservation(scheduledReservationService.updateStatus(reservationId, status));
    }

    public ReservationResponse updateWalkInReservationStatus(Long reservationId, ReservationStatus status) {
        return ReservationResponse.fromWalkInStay(walkInStayService.updateStatus(reservationId, status));
    }

    public ReservationResponse extendWalkInReservation(Long reservationId, Integer extraHours) {
        return ReservationResponse.fromWalkInStay(walkInStayService.extend(reservationId, extraHours));
    }

    public Duration getRemainingTime(Long reservationId) {
        return walkInStayService.getRemainingTime(reservationId);
    }

}
