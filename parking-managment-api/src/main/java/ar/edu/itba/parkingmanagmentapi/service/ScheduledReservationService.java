package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledReservationService {

    ScheduledReservation create(ScheduledReservation scheduledReservation);

    ScheduledReservation updateStatus(Long reservationId, ReservationStatus status);

    ScheduledReservation findById(Long id);

    List<ScheduledReservation> findBySpotIdAndOverlappingPeriod(Long spotId, DateTimeRange period);

    Page<ScheduledReservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable);

    List<ScheduledReservation> checkInReservation(LocalDateTime checkInTime);
}
