package ar.edu.itba.parkingmanagmentapi.repositoryDomain;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;

import java.util.Optional;

public interface VehicleDomainRepository {
    VehicleDomain save(VehicleDomain vehicle);
    Optional<VehicleDomain> findByLicensePlate(String licensePlate);
}