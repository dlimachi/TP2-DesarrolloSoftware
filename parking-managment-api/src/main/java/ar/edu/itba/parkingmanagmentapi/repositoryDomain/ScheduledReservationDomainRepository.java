package ar.edu.itba.parkingmanagmentapi.repositoryDomain;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledReservationDomainRepository {

    Reservation save(ScheduledReservation scheduledReservation);
    Reservation findById(Long id);
    Reservation update(Long id, ReservationStatus status);
    Page<Reservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable);

    List<Reservation> findBySpotIdAndOverlappingPeriod(Long spotId, DateTimeRange period);

    List<Reservation> checkInReservation(LocalDateTime checkInTime);

}
