package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.ParkingPrice;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingPriceRepository;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class DomainParkingPriceRepositoryImpl implements DomainParkingPriceRepository {

    private final ParkingPriceRepository parkingPriceRepository;

    @Override
    public ParkingPrice save(ParkingPrice parkingPrice) {
        return parkingPriceRepository.save(parkingPrice);
    }

    @Override
    public ParkingPrice findById(Long id) {
        return parkingPriceRepository.findById(id).orElseThrow(() -> new NotFoundException("ParkingPrice not found"));
    }

    @Override
    public List<ParkingPrice> findByParkingLotIdAndVehicleType(Long parkingLotId, VehicleType type) {
        return parkingPriceRepository.findByParkingLotIdAndVehicleType(parkingLotId, type);
    }

    @Override
    public boolean existsActivePrice(Long parkingLotId, VehicleType type) {
        return !parkingPriceRepository.findByParkingLotIdAndVehicleType(parkingLotId, type).isEmpty();
    }

    @Override
    public void delete(ParkingPrice parkingPrice) {
        parkingPriceRepository.delete(parkingPrice);
    }
}