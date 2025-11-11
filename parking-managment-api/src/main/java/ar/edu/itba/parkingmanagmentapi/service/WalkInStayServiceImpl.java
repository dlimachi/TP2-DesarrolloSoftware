package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.config.AppConstants;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.repositoryDomain.WalkInStayDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WalkInStayServiceImpl implements WalkInStayService {
    private final WalkInStayDomainRepository walkInStayDomainRepository;

    @Override
    public Reservation updateReservation(Reservation reservation) {
        return walkInStayDomainRepository.update(reservation);
    }

    @Override
    public Reservation createReservation(Reservation reservation, Spot spot, UserVehicleAssignment assignment) {
        return walkInStayDomainRepository.save(reservation, spot, assignment);
    }

    @Override
    public Reservation findById(Long id) {
        return walkInStayDomainRepository.findById(id);
    }

    @Override
    public Reservation updateStatus(Long id, ReservationStatus status) {
        return walkInStayDomainRepository.updateStatus(id, status);
    }

    @Override
    public Page<Reservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable) {
        return walkInStayDomainRepository.findByCriteria(reservationCriteria, pageable);
    }

    @Override
    @Transactional
    public Reservation extend(Long id, int extraHours) {
        return walkInStayDomainRepository.extend(id, extraHours);
    }

    @Override
    public Duration getRemainingTime(Long id) {
        Reservation reservation = walkInStayDomainRepository.findById(id);
        return Duration.between(LocalDateTime.now(), reservation.getRange().getEnd());
    }

    @Override
    public List<Reservation> getExpiringReservations() {
        return walkInStayDomainRepository.getExpiringReservations(
                LocalDateTime.now().plusMinutes(AppConstants.EXPIRING_RESERVATION_THRESHOLD_MINUTES));
    }

}
