package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import ar.edu.itba.parkingmanagmentapi.repository.SpotRepository;
import ar.edu.itba.parkingmanagmentapi.repository.SpotSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DomainSpotRepositoryImpl implements DomainSpotRepository{

    private final SpotRepository spotRepository;

    //TODO: Use ParkingLotDomain class
    @Override
    public boolean existsByParkingLotAndFloorAndCode(ParkingLotDomain parkingLotDomain, int floor, String code) {
        return spotRepository.existsByParkingLotAndFloorAndCode(parkingLotDomain.toEntity(), floor, code);
    }

    //TODO: Use ParkingLotDomain class
    @Override
    public boolean existsByParkingLotAndFloorAndCodeAndIdNot(ParkingLotDomain parkingLotDomain, Integer floor, String code, Long id) {
        return spotRepository.existsByParkingLotAndFloorAndCodeAndIdNot(parkingLotDomain.toEntity(), floor, code, id);
    }

    @Override
    public SpotDomain save(SpotDomain spotDomain) {
        return SpotDomain.fromEntity(spotRepository.save(spotDomain.toEntity()));
    }

    @Override
    public Optional<SpotDomain> findById(Long id) {
        return spotRepository.findById(id).map(SpotDomain::fromEntity);
    }

    @Override
    public void delete(SpotDomain spotDomain) {
        spotRepository.delete(spotDomain.toEntity());
    }

    @Override
    public Page<SpotDomain> findAll(Long parkingLotId, Boolean available, VehicleType vehicleType, Integer floor, Boolean isAccessible, Boolean isReservable, Pageable pageable) {
        Page<Spot> spotPage = spotRepository.findAll(SpotSpecifications.withFilters(parkingLotId, available, vehicleType, floor, isAccessible, isReservable), pageable);
        return spotPage.map(SpotDomain::fromEntity);
    }

}
