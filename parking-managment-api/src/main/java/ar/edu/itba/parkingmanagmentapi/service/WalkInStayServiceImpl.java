package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.config.AppConstants;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.domain.ReservationCriteria;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.mapper.persistence.WalkInStayMapper;
import ar.edu.itba.parkingmanagmentapi.model.WalkInStay;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStayRepository;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStaySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalkInStayServiceImpl implements WalkInStayService {

    private final WalkInStayRepository walkInStayRepository;
    private final WalkInStayMapper walkInStayMapper;

    public WalkInStayServiceImpl(WalkInStayRepository walkInStayRepository, WalkInStayMapper walkInStayMapper) {
        this.walkInStayRepository = walkInStayRepository;
        this.walkInStayMapper = walkInStayMapper;
    }

    @Override
    public Reservation updateReservation(WalkInStay walkInStay) {
        return walkInStayMapper.toDomain(walkInStayRepository.save(walkInStay));
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        return walkInStayMapper.toDomain(walkInStayRepository.save(walkInStayMapper.toEntity(reservation)));
    }

    @Override
    public Reservation findById(Long id) {
        return walkInStayMapper.toDomain(walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay with id " + id + " not found")));
    }

    @Override
    public Reservation updateStatus(Long id, ReservationStatus status) {
        WalkInStay walkInStay = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay with id " + id + " not found"));

        walkInStay.setStatus(status);

        return walkInStayMapper.toDomain(walkInStayRepository.save(walkInStay));
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
                .map(walkInStayMapper::toDomain);
    }

    @Override
    @Transactional
    public Reservation extend(Long id, int extraHours) {
        WalkInStay stay = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay not found"));

        stay.setExpectedEndTime(stay.getExpectedEndTime().plusHours(extraHours));
        return walkInStayMapper.toDomain(walkInStayRepository.save(stay));
    }

    @Override
    public Duration getRemainingTime(Long id) {
        WalkInStay stay = walkInStayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Walk-in stay not found"));

        return Duration.between(LocalDateTime.now(), stay.getExpectedEndTime());
    }

    @Override
    public List<Reservation> getExpiringReservations() {
        return walkInStayRepository.findExpiringSoon(LocalDateTime.now().plusMinutes(AppConstants.EXPIRING_RESERVATION_THRESHOLD_MINUTES))
                .stream()
                .map(walkInStayMapper::toDomain)
                .collect(Collectors.toList());
    }

}
