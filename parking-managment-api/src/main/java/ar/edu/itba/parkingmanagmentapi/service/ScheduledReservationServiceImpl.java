package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationSpecifications;
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
    public ScheduledReservation updateStatus(Long id, ReservationStatus status) {
        return scheduledReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));
    }

    @Override
    public Page<ScheduledReservation> findByUserId(Long userId, ReservationStatus status, String vehiclePlate, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return scheduledReservationRepository.findAll(ScheduledReservationSpecifications.withFilters(userId, null, status, vehiclePlate, from, to), pageable);
    }

    @Override
    public Page<ScheduledReservation> findByParkingLotId(Long parkingLotId, ReservationStatus status, String licensePlate, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return scheduledReservationRepository.findAll(ScheduledReservationSpecifications.withFilters(null, parkingLotId, status, null, from, to), pageable);
    }

    @Override
    public List<ScheduledReservation> checkInReservation(LocalDateTime checkInTime) {
        return scheduledReservationRepository
                .findByStatusAndReservedStartTimeLessThanEqual(ReservationStatus.PENDING, checkInTime);
    }

}
