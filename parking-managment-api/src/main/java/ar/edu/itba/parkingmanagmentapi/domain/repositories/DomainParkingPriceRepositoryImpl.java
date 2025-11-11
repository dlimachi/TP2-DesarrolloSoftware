package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingPriceDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.ParkingPrice;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingPriceRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingPriceSpecifications;
import ar.edu.itba.parkingmanagmentapi.util.ParkingPriceFilter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class DomainParkingPriceRepositoryImpl implements DomainParkingPriceRepository {

    private final ParkingPriceRepository parkingPriceRepository;

    private ParkingPrice findEntityById(Long id) {
        if (parkingPriceRepository.findById(id).isEmpty()) {
            throw new NotFoundException("ParkingPrice with id " + id + " not found");
        }
        return parkingPriceRepository.findById(id).get();
    }

    @Override
    public ParkingPriceDomain save(ParkingPriceDomain domain) {
        ParkingPrice entity = domain.toEntity();
        ParkingPrice saved = parkingPriceRepository.save(entity);
        return ParkingPriceDomain.fromEntity(saved);
    }

    @Override
    public ParkingPriceDomain update(ParkingPriceDomain domain) {
        ParkingPrice existing = this.findEntityById(domain.getId());

        existing.setVehicleType(domain.getVehicleType());
        existing.setPrice(domain.getPrice());
        existing.setValidFrom(domain.getValidFrom());
        existing.setValidTo(domain.getValidTo());
        existing.setParkingLot(domain.getParkingLot().toEntity());
        ParkingPrice updated = parkingPriceRepository.save(existing);

        return ParkingPriceDomain.fromEntity(updated);
    }

    @Override
    public void deleteById(Long id) { parkingPriceRepository.deleteById(id); }

    @Override
    public ParkingPriceDomain findById(Long id) {
        ParkingPrice entity = parkingPriceRepository.findById(id).orElseThrow(() -> new NotFoundException("ParkingPrice not found"));
        return ParkingPriceDomain.fromEntity(entity);
    }

    @Override
    public List<ParkingPriceDomain> findByParkingLotIdAndVehicleType(Long parkingLotId, VehicleType type) {
        return parkingPriceRepository.findByParkingLotIdAndVehicleType(parkingLotId, type)
                .stream()
                .map(ParkingPriceDomain::fromEntity)
                .toList();
    }

    @Override
    public boolean existsActivePrice(Long parkingLotId, VehicleType type) {
        return !parkingPriceRepository.findByParkingLotIdAndVehicleType(parkingLotId, type).isEmpty();
    }

    @Override
    public List<ParkingPriceDomain> findByFilters(Long parkingLotId, ParkingPriceFilter filter) {
        Specification<ParkingPrice> spec = ParkingPriceSpecifications.withFilters(
                parkingLotId,
                filter.getMin(),
                filter.getMax(),
                filter.getVehicleType(),
                filter.getFrom(),
                filter.getTo()
        );

        Sort sortSpec = "desc".equalsIgnoreCase(filter.getSort())
                ? Sort.by(Sort.Direction.DESC, "price")
                : Sort.by(Sort.Direction.ASC, "price");

        return parkingPriceRepository.findAll(spec, sortSpec)
                .stream()
                .map(ParkingPriceDomain::fromEntity)
                .toList();
    }

}
