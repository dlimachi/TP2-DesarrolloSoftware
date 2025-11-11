package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import ar.edu.itba.parkingmanagmentapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DomainSpotRepositoryImpl implements DomainSpotRepository{

    private final SpotRepository spotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ScheduledReservationRepository scheduledReservationRepository;
    private final WalkInStayRepository walkInStayRepository;

    @Override
    public boolean existsByParkingLotAndFloorAndCode(ParkingLotDomain parkingLotDomain, int floor, String code) {
        return spotRepository.existsByParkingLotAndFloorAndCode(parkingLotDomain.toEntity(), floor, code);
    }

    @Override
    public boolean existsByParkingLotAndFloorAndCodeAndIdNot(ParkingLotDomain parkingLotDomain, Integer floor, String code, Long id) {
        return spotRepository.existsByParkingLotAndFloorAndCodeAndIdNot(parkingLotDomain.toEntity(), floor, code, id);
    }

    @Override
    public SpotDomain save(SpotDomain spotDomain) {
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(spotDomain.getParkingLotId());
        return SpotDomain.fromEntity(spotRepository.save(spotDomain.toEntity(parkingLot.get())));
    }

    @Override
    public Optional<SpotDomain> findById(Long id) {
        return spotRepository.findById(id).map(SpotDomain::fromEntity);
    }

    @Override
    public void delete(SpotDomain spotDomain, ParkingLotDomain parkingLotDomain) {
        ParkingLot entity = parkingLotDomain.toEntity();
        //List<SpotDomain> spotsDomain = findAll(entity.getId());
        //List<Spot> spots = spotsDomain.stream().map(spotDomain1 -> spotDomain1.toEntity(entity)).toList();
        //entity.setSpots(spots);
        spotRepository.delete(spotDomain.toEntity(entity));
    }


    @Override
    public Page<SpotDomain> findAll(Long parkingLotId, Boolean available, VehicleType vehicleType, Integer floor, Boolean isAccessible, Boolean isReservable, Pageable pageable) {
        Page<Spot> spotPage = spotRepository.findAll(SpotSpecifications.withFilters(parkingLotId, available, vehicleType, floor, isAccessible, isReservable), pageable);
        return spotPage.map(SpotDomain::fromEntity);
    }

    @Override
    public List<SpotDomain> findAll(Long parkingLotId) {
        List<SpotDomain> spots = spotRepository.findAll(SpotSpecifications.withFilters(parkingLotId, null, null, null, null, null))
                .stream()
                .map(SpotDomain::fromEntity)
                .toList();
        return spots;
    }

}
