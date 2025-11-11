package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.mapper.persistence.ScheduledReservationMapper;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduledReservationServiceImpl implements ScheduledReservationService {

    private final ScheduledReservationRepository scheduledReservationRepository;
    private final ScheduledReservationMapper scheduledReservationMapper;

    public ScheduledReservationServiceImpl(ScheduledReservationRepository scheduledReservationRepository, ScheduledReservationMapper scheduledReservationMapper) {
        this.scheduledReservationRepository = scheduledReservationRepository;
        this.scheduledReservationMapper = scheduledReservationMapper;
    }

    @Override
    public Reservation create(ScheduledReservation scheduledReservation) {
        return scheduledReservationMapper.toDomain(scheduledReservationRepository.save(scheduledReservation));
    }

    @Override
    public Reservation findById(Long id) {
        return scheduledReservationMapper.toDomain(scheduledReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found")));
    }

    @Override
    public List<Reservation> findBySpotIdAndOverlappingPeriod(Long spotId, DateTimeRange period) {
        return scheduledReservationRepository.findBySpotIdAndReservedStartTimeLessThanEqualAndExpectedEndTimeGreaterThanEqual(spotId, period.getStart(), period.getEnd())
                .stream()
                .map(scheduledReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Reservation updateStatus(Long id, ReservationStatus status) {
        ScheduledReservation scheduledReservation = scheduledReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));

        scheduledReservation.setStatus(status);

        return scheduledReservationMapper.toDomain(scheduledReservationRepository.save(scheduledReservation));
    }

    @Override
    public Page<Reservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable) {
        return scheduledReservationRepository.findAll(
                ScheduledReservationSpecifications.withFilters(
                        reservationCriteria.getUserId(),
                        reservationCriteria.getParkingLotId(),
                        reservationCriteria.getStatus(),
                        reservationCriteria.getLicensePlate(),
                        reservationCriteria.getRange().getStart(),
                        reservationCriteria.getRange().getEnd()),
                pageable
        ).map(scheduledReservationMapper::toDomain);
    }

    @Override
    public List<Reservation> checkInReservation(LocalDateTime checkInTime) {
        return scheduledReservationRepository
                .findByStatusAndReservedStartTimeLessThanEqual(ReservationStatus.PENDING, checkInTime)
                .stream()
                .map(scheduledReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

}
