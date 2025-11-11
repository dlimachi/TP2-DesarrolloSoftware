package ar.edu.itba.parkingmanagmentapi.service.orchestrator;

import ar.edu.itba.parkingmanagmentapi.config.AppConstants;
import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.*;
import ar.edu.itba.parkingmanagmentapi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationOrchestratorService {

    private final ScheduledReservationService scheduledReservationService;
    private final WalkInStayService walkInStayService;
    private final SpotService spotService;
    private final ParkingPriceService parkingPriceService;
    private final UserVehicleAssignmentService userVehicleAssignmentService;

    public Reservation createWalkInStayReservation(final Reservation reservation) {
        Spot spot = spotService.findEntityById(reservation.getSpotId());

        if (!parkingPriceService.existsActiveByParkingLotIdAndVehicleType(spot.getParkingLot().getId(), spot.getVehicleType())) {
            throw new NotFoundException("There are no active prices for this type of vehicle in the parking lot");
        }

        UserVehicleAssignment assignment = userVehicleAssignmentService.findOrCreateByUserIdAndLicensePlate(AppConstants.DEFAULT_USER_ID, reservation.getVehicleLicensePlate());

        WalkInStay stay = new WalkInStay();
        stay.setCheckInTime(reservation.getRange().getStart());
        stay.setExpectedEndTime(reservation.getRange().getEnd());
        stay.setStatus(ReservationStatus.ACTIVE);
        stay.setSpot(spot);
        stay.setCheckOutTime(null);
        stay.setUserVehicleAssignment(assignment);

        spotService.toggleAvailability(spot.getId());

        return walkInStayService.createReservation(reservation);
    }

    public Reservation createScheduledReservation(final Reservation reservation) {
        Spot spot = spotService.findEntityById(reservation.getSpotId());

        var dateRange = DateTimeRange.from(reservation.getRange().getStart(), reservation.getRange().getEnd());

        List<Reservation> overlapping = scheduledReservationService.findBySpotIdAndOverlappingPeriod(
                spot.getId(),
                dateRange
        );

        if (!overlapping.isEmpty()) {
            throw new BadRequestException("spot.not.available", spot.getId());
        }

        BigDecimal estimatedPrice = parkingPriceService.calculateEstimatedPrice(spot.getParkingLot().getId(), spot.getVehicleType(), dateRange);

        UserVehicleAssignment assignment = userVehicleAssignmentService.findOrCreateByUserIdAndLicensePlate(reservation.getUserId(), reservation.getVehicleLicensePlate());

        ScheduledReservation scheduledReservation = new ScheduledReservation();
        scheduledReservation.setReservedStartTime(reservation.getRange().getStart());
        scheduledReservation.setExpectedEndTime(reservation.getRange().getEnd());
        scheduledReservation.setEstimatedPrice(estimatedPrice);
        scheduledReservation.setStatus(ReservationStatus.PENDING);
        scheduledReservation.setSpot(spot);
        scheduledReservation.setUserVehicleAssignment(assignment);

        return scheduledReservationService.create(scheduledReservation);
    }

    public Reservation getScheduledReservationById(final Long reservationId) {
        return scheduledReservationService.findById(reservationId);
    }

    public Reservation getWalkInStayReservationById(final Long reservationId) {
        return walkInStayService.findById(reservationId);
    }

    public Page<Reservation> getScheduledReservations(final ReservationCriteria reservationCriteria, Pageable pageable) {
        return scheduledReservationService.findByCriteria(reservationCriteria, pageable);
    }

    public Page<Reservation> getWalkInStayReservations(final ReservationCriteria reservationCriteria, Pageable pageable) {
        return walkInStayService.findByCriteria(reservationCriteria, pageable);
    }

    public Reservation updateScheduledReservationStatus(Long reservationId, ReservationStatus status) {
        return scheduledReservationService.updateStatus(reservationId, status);
    }

    public Reservation updateWalkInReservationStatus(Long reservationId, ReservationStatus status) {
        return walkInStayService.updateStatus(reservationId, status);
    }

    public Reservation extendWalkInReservation(Long reservationId, Integer extraHours) {
        return walkInStayService.extend(reservationId, extraHours);
    }

    public Duration getRemainingTime(Long reservationId) {
        return walkInStayService.getRemainingTime(reservationId);
    }

}
