package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.ReservationResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ScheduledReservationRequest;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingPriceRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationSpecifications;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStaySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledReservationServiceImpl implements ScheduledReservationService {

    private final ScheduledReservationRepository scheduledReservationRepository;

    public ScheduledReservationServiceImpl(ScheduledReservationRepository scheduledReservationRepository) {
        this.scheduledReservationRepository = scheduledReservationRepository;
    }

    @Override
    public ScheduledReservation create(ScheduledReservation scheduledReservation) {
        return scheduledReservationRepository.save(scheduledReservation);
    }

    @Override
    public ScheduledReservation findById(Long id) {
        return scheduledReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));
    }

    @Override
    public List<ScheduledReservation> findBySpotIdAndOverlappingPeriod(Long spotId, DateTimeRange period) {
        return scheduledReservationRepository.findBySpotIdAndReservedStartTimeLessThanEqualAndExpectedEndTimeGreaterThanEqual(spotId, period.getStart(), period.getEnd());
    }

    @Override
    public ScheduledReservation updateStatus(Long id, ReservationStatus status) {
        ScheduledReservation scheduledReservation = scheduledReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));

        scheduledReservation.setStatus(status);

        return scheduledReservationRepository.save(scheduledReservation);
    }

    @Override
    public Page<ScheduledReservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable) {
        return scheduledReservationRepository.findAll(
                ScheduledReservationSpecifications.withFilters(
                        reservationCriteria.getUserId(),
                        reservationCriteria.getParkingLotId(),
                        reservationCriteria.getStatus(),
                        reservationCriteria.getLicensePlate(),
                        reservationCriteria.getRange().getStart(),
                        reservationCriteria.getRange().getEnd()),
                pageable
        );
    }

    @Override
    public List<ScheduledReservation> checkInReservation(LocalDateTime checkInTime) {
        return scheduledReservationRepository
                .findByStatusAndReservedStartTimeLessThanEqual(ReservationStatus.PENDING, checkInTime);
    }

}
