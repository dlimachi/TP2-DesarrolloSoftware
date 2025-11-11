package ar.edu.itba.parkingmanagmentapi.repositoryDomain;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;
import ar.edu.itba.parkingmanagmentapi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VehicleDomainRepositoryImpl implements VehicleDomainRepository {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleDomain save(VehicleDomain domain, UserVehicleAssignment vehicleAssignment) {
        var vehicle = domain.toEntity();
        if (vehicleAssignment != null) {
            vehicle.getUserAssignments().add(vehicleAssignment);
        }
        vehicle = vehicleRepository.save(vehicle);

        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleDomain.fromEntity(saved);
    }

    @Override
    public VehicleDomain findByLicensePlate(String licensePlate) {
        return vehicleRepository.findById(licensePlate)
                .map(VehicleDomain::fromEntity)
                .orElseThrow(() -> new NotFoundException("vehicle.not.found", licensePlate));
    }

    @Override
    public List<VehicleDomain> findAllByUserId(Long userId) {
        var vehicles = vehicleRepository.findAllByUserId(userId);
          return vehicles.stream()
                 .map(VehicleDomain::fromEntity)
                 .toList();
    }

    @Override
    public void deleteById(String licensePlate) {
        vehicleRepository.deleteById(licensePlate);
    }

    @Override
    public VehicleDomain update(VehicleDomain request) {
        Vehicle existing = findByLicensePlate(request.licensePlate()).toEntity();

        Vehicle updatedVehicle = vehicleRepository.save(new Vehicle(
                existing.getLicensePlate(),
                request.brand() != null ? request.brand() : existing.getBrand(),
                request.model() != null ? request.model() : existing.getModel(),
                request.type() != null ? request.type() : existing.getType()
        ));

        return VehicleDomain.fromEntity(updatedVehicle);
    }

    @Override
    public boolean findOwnerOfVehicle(Long userId, String licensePlate) {
        return vehicleRepository.findById(licensePlate)
                .map(vehicle -> vehicle.getUserAssignments().stream()
                        .anyMatch(assignment -> assignment.getUser().getId().equals(userId)))
                .orElse(false);
    }


}
