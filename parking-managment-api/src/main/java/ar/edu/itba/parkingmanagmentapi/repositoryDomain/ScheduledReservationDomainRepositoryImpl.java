package ar.edu.itba.parkingmanagmentapi.repositoryDomain;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ScheduledReservationDomainRepositoryImpl implements ScheduledReservationDomainRepository {
    private final ScheduledReservationRepository scheduledReservationRepository;


    @Override
    public Reservation save(ScheduledReservation scheduledReservation) {
        return Reservation.toDomain(scheduledReservationRepository.save(scheduledReservation));
    }

    @Override
    public Reservation findById(Long id) {
        var entity = scheduledReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        return Reservation.toDomain(entity);
    }

    @Override
    public Reservation update(Long id, ReservationStatus status) {
        ScheduledReservation scheduledReservation = scheduledReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));

        scheduledReservation.setStatus(status);

        return Reservation.toDomain(scheduledReservationRepository.save(scheduledReservation));
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
        ).map(Reservation::toDomain);
    }

    @Override
    public List<Reservation> findBySpotIdAndOverlappingPeriod(Long spotId, DateTimeRange period) {
        return scheduledReservationRepository.findBySpotIdAndReservedStartTimeLessThanEqualAndExpectedEndTimeGreaterThanEqual(spotId, period.getStart(), period.getEnd())
                .stream()
                .map(Reservation::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> checkInReservation(LocalDateTime checkInTime) {
        return scheduledReservationRepository
                .findByStatusAndReservedStartTimeLessThanEqual(ReservationStatus.PENDING, checkInTime)
                .stream()
                .map(Reservation::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySpotIdAndReservedStartTimeAfter(Long spotId, LocalDateTime time) {
        return scheduledReservationRepository.existsBySpotIdAndReservedStartTimeAfter(spotId, time);
    }

    @Override
    public void updateSpotSnapshot(Long spotId, String code, Integer floor) {
        scheduledReservationRepository.updateSpotSnapshot(spotId, code, floor);
    }
}
