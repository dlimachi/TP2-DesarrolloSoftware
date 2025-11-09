package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.dto.ParkingLotResponse;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingLotRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import ar.edu.itba.parkingmanagmentapi.security.service.SecurityService;
import ar.edu.itba.parkingmanagmentapi.util.ParkingLotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ScheduledReservationRepository scheduledReservationRepository;
    private final SecurityService securityService;

    @Override
    public ParkingLotResponse createParkingLot(ParkingLotDomain parkingLot) {
        Manager currentManager = securityService.getCurrentManager().get();

        // TODO: probably need a ParkingLotDomain::fromDto, for that we probably
        // also need the Spot domain class though.
        ParkingLot parkingLotDto = parkingLotRepository.save(parkingLot.toPersistence(currentManager));
        return ParkingLotMapper.toParkingLotResponse(parkingLotDto);
    }


    // TODO: no clue... Should I set the parkingLotDto values in the repository?
    // But then I'd need to pass the parkingLotDomain or something to it.
    @Override
    public ParkingLotResponse updateParkingLot(Long id, ParkingLotDomain request) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ParkingLot with id " + id + " not found"));

        parkingLot.setName(request.name());
        parkingLot.setAddress(request.address());
        parkingLot.setImageUrl(request.imageUrl());
        parkingLot.setLatitude(request.latitude());
        parkingLot.setLongitude(request.longitude());

        ParkingLot parkingLotDto = parkingLotRepository.save(parkingLot);
        return ParkingLotMapper.toParkingLotWithoutSpotsResponse(parkingLotDto);
    }

    @Override
    public ParkingLotResponse findById(Long id) {
        return parkingLotRepository.findById(id)
                .map(ParkingLotMapper::toParkingLotWithoutSpotsResponse)
                .orElseThrow(() -> new NotFoundException("ParkingLot not found"));
    }

    @Override
    public List<ParkingLotResponse> findAll() {
        return parkingLotRepository.findAll()
                .stream()
                .map(ParkingLotMapper::toParkingLotWithoutSpotsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteParkingLot(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parking lot not found"));

        boolean hasFutureScheduledReservations = scheduledReservationRepository.existsBySpotParkingLotIdAndReservedStartTimeAfter(
                id, LocalDateTime.now());

        boolean hasUnavailableSpots = parkingLot.getSpots().stream().anyMatch(spot -> !spot.getIsAvailable());

        if (hasUnavailableSpots || hasFutureScheduledReservations) {
            throw new BadRequestException("Cannot delete parking lot: it has active or future reservations");
        }
        parkingLotRepository.deleteById(id);
    }

    @Override
    public ParkingLot findEntityById(Long id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ParkingLot not found"));
    }

    @Override
    public Optional<User> getManagerOfParkingLot(Long parkingLotId) {
        return Optional.ofNullable(parkingLotRepository.findById(parkingLotId)
                .map(ParkingLot::getManager)
                .map(Manager::getUser)
                .orElseThrow(() -> new AuthorizationDeniedException("Manager is not authorized to access this parking lot"))
        );
    }

    @Override
    public List<ParkingLotResponse> findByUserId(Long userId) {
        return parkingLotRepository.findByManagerUserId(userId)
                .stream()
                .map(ParkingLotMapper::toParkingLotWithoutSpotsResponse)
                .collect(Collectors.toList());
    }

}
