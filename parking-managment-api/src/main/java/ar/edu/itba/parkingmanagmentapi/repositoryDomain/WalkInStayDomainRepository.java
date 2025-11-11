package ar.edu.itba.parkingmanagmentapi.repositoryDomain;

import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface WalkInStayDomainRepository {

    Reservation save(Reservation reservation, Spot spot, UserVehicleAssignment assignment);

    Reservation update(Reservation reservation);

    Reservation findById(Long id);

    Reservation updateStatus(Long id, ReservationStatus status);

    Page<Reservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable);

    Reservation extend(Long id, int extraHours);

    List<Reservation> getExpiringReservations(LocalDateTime limitTime);

    void updateSpotSnapshot(Long spotId, String code, Integer floor);
}
