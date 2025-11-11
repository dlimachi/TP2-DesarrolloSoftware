package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledReservationService {

    Reservation create(ScheduledReservation scheduledReservation);

    Reservation updateStatus(Long reservationId, ReservationStatus status);

    Reservation findById(Long id);

    List<Reservation> findBySpotIdAndOverlappingPeriod(Long spotId, DateTimeRange period);

    Page<Reservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable);

    List<Reservation> checkInReservation(LocalDateTime checkInTime);
}
