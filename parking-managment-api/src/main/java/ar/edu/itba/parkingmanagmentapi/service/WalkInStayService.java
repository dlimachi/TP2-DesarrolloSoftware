package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.util.List;

public interface WalkInStayService {

    Reservation createReservation(Reservation reservation, Spot spot, UserVehicleAssignment assignment);

    Reservation updateReservation(Reservation walkInStay);

    Reservation findById(Long walkInStayId);

    Reservation updateStatus(Long id, ReservationStatus status);

    Page<Reservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable);

    Reservation extend(Long id, int extraHours);

    Duration getRemainingTime(Long id);

     List<Reservation> getExpiringReservations();
}
