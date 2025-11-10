package ar.edu.itba.parkingmanagmentapi.repositoryDomain;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;
import ar.edu.itba.parkingmanagmentapi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VehicleDomainRepositoryImpl implements VehicleDomainRepository {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleDomain save(VehicleDomain domain) {
        Vehicle saved = vehicleRepository.save(domain.toEntity());
        return VehicleDomain.fromEntity(saved);
    }

    @Override
    public Optional<VehicleDomain> findByLicensePlate(String licensePlate) {
        return vehicleRepository.findById(licensePlate)
                .map(VehicleDomain::fromEntity);
    }
}
