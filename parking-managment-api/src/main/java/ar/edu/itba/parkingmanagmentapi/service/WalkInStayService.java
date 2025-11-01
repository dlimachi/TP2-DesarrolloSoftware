package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.WalkInStay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface WalkInStayService {

    WalkInStay createReservation(WalkInStay request);

    WalkInStay getReservation(Long id);

    WalkInStay updateReservationStatus(Long id, ReservationStatus status);

    Page<WalkInStay> getReservationsByUser(Long userId, ReservationStatus status, String vehiclePlate, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<WalkInStay> getReservationsByParkingLot(Long parkingLotId, ReservationStatus status, String licensePlate, LocalDateTime from, LocalDateTime to, Pageable pageable);

    WalkInStay extendReservation(Long id, int extraHours);

    Duration getRemainingTime(Long id);

     List<WalkInStay> getExpiringReservations();
}
