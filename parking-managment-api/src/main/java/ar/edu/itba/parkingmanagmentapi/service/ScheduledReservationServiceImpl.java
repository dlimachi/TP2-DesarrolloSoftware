package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import ar.edu.itba.parkingmanagmentapi.repositoryDomain.ScheduledReservationDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledReservationServiceImpl implements ScheduledReservationService {

    private final ScheduledReservationDomainRepository scheduledReservationDomainRepository;

    @Override
    public Reservation create(ScheduledReservation scheduledReservation) {
        return scheduledReservationDomainRepository.save(scheduledReservation);
    }

    @Override
    public Reservation findById(Long id) {
        return scheduledReservationDomainRepository.findById(id);
    }

    @Override
    public List<Reservation> findBySpotIdAndOverlappingPeriod(Long spotId, DateTimeRange period) {
        return scheduledReservationDomainRepository.findBySpotIdAndOverlappingPeriod(spotId, period);
    }

    @Override
    public Reservation updateStatus(Long id, ReservationStatus status) {
        return scheduledReservationDomainRepository.update(id, status);
    }

    @Override
    public Page<Reservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable) {
        return scheduledReservationDomainRepository.findByCriteria(reservationCriteria, pageable);
    }

    @Override
    public List<Reservation> checkInReservation(LocalDateTime checkInTime) {
        return scheduledReservationDomainRepository.checkInReservation(checkInTime);
    }

}
