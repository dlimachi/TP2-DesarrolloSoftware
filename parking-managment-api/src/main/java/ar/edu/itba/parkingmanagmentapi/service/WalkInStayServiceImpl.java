package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.config.AppConstants;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.ReservationResponse;
import ar.edu.itba.parkingmanagmentapi.dto.WalkInStayRequest;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;
import ar.edu.itba.parkingmanagmentapi.model.WalkInStay;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingPriceRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStayRepository;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStaySpecifications;
import ar.edu.itba.parkingmanagmentapi.validators.WalkInStayRequestValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalkInStayServiceImpl implements WalkInStayService {

    private final WalkInStayRepository walkInStayRepository;

    public WalkInStayServiceImpl(WalkInStayRepository walkInStayRepository) {
        this.walkInStayRepository = walkInStayRepository;
    }

    @Override
    public WalkInStay updateReservation(WalkInStay walkInStay) {
        return walkInStayRepository.save(walkInStay);
    }

    @Override
    public WalkInStay createReservation(WalkInStay walkInStay) {
        return walkInStayRepository.save(walkInStay);
    }

    @Override
    public WalkInStay findById(Long id) {
        return walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay with id " + id + " not found"));
    }

    @Override
    public WalkInStay updateStatus(Long id, ReservationStatus status) {
        WalkInStay walkInStay = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay with id " + id + " not found"));

        walkInStay.setStatus(status);

        return walkInStayRepository.save(walkInStay);
    }

    @Override
    public Page<WalkInStay> findByCriteria(ReservationCriteria reservationCriteria, Pageable pageable) {
        return walkInStayRepository.findAll(
                        WalkInStaySpecifications.withFilters(
                                reservationCriteria.getUserId(),
                                reservationCriteria.getParkingLotId(),
                                reservationCriteria.getStatus(),
                                reservationCriteria.getLicensePlate(),
                                reservationCriteria.getRange().getStart(),
                                reservationCriteria.getRange().getEnd()),
                        pageable
                );
    }

    @Override
    @Transactional
    public WalkInStay extend(Long id, int extraHours) {
        WalkInStay stay = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay not found"));

        stay.setExpectedEndTime(stay.getExpectedEndTime().plusHours(extraHours));
        return walkInStayRepository.save(stay);
    }

    @Override
    public Duration getRemainingTime(Long id) {
        WalkInStay stay = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay not found"));

        return Duration.between(LocalDateTime.now(), stay.getExpectedEndTime());
    }

    @Override
    public List<WalkInStay> getExpiringReservations() {
        return walkInStayRepository.findExpiringSoon(LocalDateTime.now().plusMinutes(AppConstants.EXPIRING_RESERVATION_THRESHOLD_MINUTES));
    }

}
