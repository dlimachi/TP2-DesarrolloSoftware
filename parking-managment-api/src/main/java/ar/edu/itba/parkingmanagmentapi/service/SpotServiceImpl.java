package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.domain.repositories.DomainSpotRepository;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import ar.edu.itba.parkingmanagmentapi.repository.SpotRepository;
import ar.edu.itba.parkingmanagmentapi.repository.SpotSpecifications;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotServiceImpl implements SpotService {

    private final SpotRepository spotRepository;
    private final DomainSpotRepository domainSpotRepository;
    private final ParkingLotService parkingLotService;
    private final ScheduledReservationRepository scheduledReservationRepository;
    private final WalkInStayRepository walkInStayRepository;



    @Override
    public SpotDomain createSpot(Long parkingLotId, SpotDomain spotDomain) {

        ParkingLot parkingLotDomain = parkingLotService.findEntityById(parkingLotId);

        if (domainSpotRepository.existsByParkingLotAndFloorAndCode(parkingLotDomain, spotDomain.getFloor(), spotDomain.getCode())) {
            throw new BadRequestException("spot.already.exists", spotDomain.getCode(), spotDomain.getFloor());
        }

        spotDomain.setParkingLot(parkingLotDomain);
        spotDomain.setIsReservable(spotDomain.getIsReservable());

        return domainSpotRepository.save(spotDomain);
    }

    @Override
    public SpotDomain findById(Long parkingLotId, Long id) {
        return domainSpotRepository.findById(id)
                .filter(spot -> spot.getParkingLot().getId().equals(parkingLotId))
                .orElseThrow(() -> new NotFoundException("spot.not.found", id));
    }

    @Override
    @Transactional
    public SpotDomain updateSpot(Long parkingLotId, Long id, SpotDomain updateSpot) {

        SpotDomain spot = domainSpotRepository.findById(id)
                .filter(s -> s.getParkingLot().getId().equals(parkingLotId))
                .orElseThrow(() -> new NotFoundException("spot.not.found", id));

        if (domainSpotRepository.existsByParkingLotAndFloorAndCodeAndIdNot(parkingLotService.findEntityById(parkingLotId), updateSpot.getFloor(), updateSpot.getCode(), id)) {
            throw new BadRequestException("spot.already.exists", updateSpot.getCode(), updateSpot.getFloor());
        }

        //TODO: Porque pueden cambiar todos los campos, porque cambiarias el tipo de vehículo que podes estacionar o el código del spot?
        spot.setVehicleType(VehicleType.fromName(updateSpot.getVehicleType().getName()));
        spot.setCode(updateSpot.getCode());
        spot.setFloor(updateSpot.getFloor());
        spot.setIsReservable(updateSpot.getIsReservable());
        spot.setIsAccessible(updateSpot.getIsAccessible());

        return domainSpotRepository.save(spot);
    }

    @Transactional
    @Override
    public void deleteSpot(Long parkingLotId, Long id) {
        SpotDomain spotDomain = domainSpotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("spot.not.found", id));

        boolean hasFutureScheduledReservations = scheduledReservationRepository.existsBySpotIdAndReservedStartTimeAfter(spotDomain.getId(), LocalDateTime.now());
        if (hasFutureScheduledReservations || !spotDomain.getIsAvailable()) {
            throw new IllegalStateException("Cannot delete spot: it has future scheduled reservations or is currently occupied");
        }
        // Actualizar los snapshots de las reservas antes de eliminarlo
        scheduledReservationRepository.updateSpotSnapshot(
                spotDomain.getId(), spotDomain.getCode(), spotDomain.getFloor()
        );
        walkInStayRepository.updateSpotSnapshot(
                spotDomain.getId(), spotDomain.getCode(), spotDomain.getFloor()
        );

        domainSpotRepository.delete(spotDomain);
    }

    @Override
    public Optional<User> getManagerOfSpot(Long spotId) {
        return Optional.of(domainSpotRepository.findById(spotId)
                        .map(spot -> {
                            ParkingLot parkingLotDomain = spot.getParkingLot();
                            if (parkingLotDomain == null) return null;
                            Manager manager = parkingLotDomain.getManager();
                            return manager != null ? manager.getUser() : null;
                        }))
                .orElseThrow(() -> new AuthorizationDeniedException("Manager is not authorized to access this spot"));
    }

    @Override
    public Page<SpotDomain> findByFilters(Long parkingLotId, Boolean available, VehicleType vehicleType, Integer floor, Boolean isAccessible, Boolean isReservable, Pageable pageable) {
        return domainSpotRepository.findAll(parkingLotId, available, vehicleType, floor, isAccessible, isReservable, pageable);
    }

    // -------------------------- RAW ENTITIES --------------------------


    @Override
    public SpotDomain findEntityById(Long spotId) {
        return domainSpotRepository.findById(spotId).orElseThrow(() -> new NotFoundException("spot.not.found", spotId));
    }

    @Override
    public SpotDomain updateEntity(SpotDomain spotDomain) {
        return domainSpotRepository.save(spotDomain);
    }

    @Override
    public boolean toggleAvailability(Long spotId) {
        SpotDomain spotDomain = findEntityById(spotId);
        spotDomain.setIsAvailable(!spotDomain.getIsAvailable());
        return domainSpotRepository.save(spotDomain).getIsAvailable();
    }

    @Override
    public boolean isAvailable(Long spotId) {
        return domainSpotRepository.findById(spotId).orElseThrow(() -> new NotFoundException("spot.not.found", spotId)).getIsAvailable();
    }

}
