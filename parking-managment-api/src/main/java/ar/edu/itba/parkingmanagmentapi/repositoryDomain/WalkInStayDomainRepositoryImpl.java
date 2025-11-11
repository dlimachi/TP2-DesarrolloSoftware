package ar.edu.itba.parkingmanagmentapi.repositoryDomain;

import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.model.WalkInStay;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStayRepository;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStaySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WalkInStayDomainRepositoryImpl implements WalkInStayDomainRepository {
    private final WalkInStayRepository walkInStayRepository;

    @Override
    public Reservation save(Reservation reservation, Spot spot, UserVehicleAssignment assignment) {
        WalkInStay stay = new WalkInStay();
        stay.setCheckInTime(reservation.getRange().getStart());
        stay.setExpectedEndTime(reservation.getRange().getEnd());
        stay.setStatus(ReservationStatus.ACTIVE);
        stay.setSpot(spot);
        stay.setCheckOutTime(null);
        stay.setUserVehicleAssignment(assignment);
        return Reservation.toDomain(walkInStayRepository.save(stay));
    }

    @Override
    public Reservation findById(Long id) {
        var entity = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay with id " + id + " not found"));
        return Reservation.toDomain(entity);
    }

    @Override
    public Reservation updateStatus(Long id, ReservationStatus status) {
        WalkInStay walkInStay = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay with id " + id + " not found"));

        walkInStay.setStatus(status);

        return Reservation.toDomain(walkInStayRepository.save(walkInStay));
    }

    @Override
    public Page<Reservation> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable) {
        return walkInStayRepository.findAll(
                        WalkInStaySpecifications.withFilters(
                                reservationCriteria.getUserId(),
                                reservationCriteria.getParkingLotId(),
                                reservationCriteria.getStatus(),
                                reservationCriteria.getLicensePlate(),
                                reservationCriteria.getRange().getStart(),
                                reservationCriteria.getRange().getEnd()),
                        pageable
                )
                .map(Reservation::toDomain);
    }

    @Override
    public Reservation extend(Long id, int extraHours) {
        WalkInStay stay = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay not found"));

        stay.setExpectedEndTime(stay.getExpectedEndTime().plusHours(extraHours));
        return Reservation.toDomain(walkInStayRepository.save(stay));
    }

    @Override
    public List<Reservation> getExpiringReservations(LocalDateTime limitTime) {
        return walkInStayRepository.findExpiringSoon(limitTime)
                .stream()
                .map(Reservation::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Reservation update(Reservation reservation) {
        WalkInStay stay = walkInStayRepository.findById(reservation.getId())
                .orElseThrow(() -> new NotFoundException("Walk-in stay with id " + reservation.getId() + " not found"));

        stay.setCheckInTime(reservation.getRange().getStart());
        stay.setExpectedEndTime(reservation.getRange().getEnd());
        stay.setStatus(reservation.getStatus());

        stay = walkInStayRepository.save(stay);

        return Reservation.toDomain(stay);
    }

    @Override
    public void updateSpotSnapshot(Long spotId, String code, Integer floor) {
        walkInStayRepository.updateSpotSnapshot(spotId, code, floor);
    }
}
