package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import ar.edu.itba.parkingmanagmentapi.dto.VehicleResponse;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;
import ar.edu.itba.parkingmanagmentapi.repository.VehicleRepository;
import ar.edu.itba.parkingmanagmentapi.util.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleDomain create(VehicleDomain request, UserVehicleAssignment vehicleAssignment) {
        var vehicle = request.toEntity();
        vehicle.getUserAssignments().add(vehicleAssignment);
        vehicle = vehicleRepository.save(vehicle);

        return VehicleDomain.fromEntity(vehicle);
    }


    @Override
    public VehicleDomain findByLicensePlate(String licensePlate) {
        var vehicle = vehicleRepository.findById(licensePlate)
                .orElseThrow(() -> new NotFoundException("vehicle.not.found", licensePlate));
        return VehicleDomain.fromEntity(vehicle);
    }

    @Override
    public List<VehicleResponse> findAllVehiclesByUser(Long id) {
        return vehicleRepository.findAllByUserId(id)
                .stream()
                .map(VehicleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public VehicleDomain update(String licensePlate, VehicleDomain request) {
        VehicleDomain existing = findByLicensePlate(licensePlate);

        VehicleDomain updated = new VehicleDomain(
                existing.licensePlate(),
                request.brand() != null ? request.brand() : existing.brand(),
                request.model() != null ? request.model() : existing.model(),
                request.type() != null ? request.type() : existing.type(),
                existing.userId()
        );
        return VehicleDomain.fromEntity(vehicleRepository.save(updated.toEntity()));
    }

    @Override
    public void delete(String licensePlate) {
        if (!vehicleRepository.existsById(licensePlate)) {
            throw new NotFoundException("vehicle.not.found", licensePlate);
        }
        vehicleRepository.deleteById(licensePlate);
    }

    @Override
    public Vehicle findEntityByLicensePlateOrCreate(Vehicle vehicle) {
        if (vehicle == null || vehicle.getLicensePlate() == null) {
            throw new IllegalArgumentException("Neither vehicle nor license plate can be null");
        }

        return vehicleRepository.findById(vehicle.getLicensePlate())
                .orElseGet(() -> vehicleRepository.save(vehicle));
    }

    @Override
    public boolean isUserOwnerOfVehicle(Long userId, String licensePlate) {
        return vehicleRepository.findById(licensePlate)
                .map(vehicle -> vehicle.getUserAssignments().stream()
                        .anyMatch(assignment -> assignment.getUser().getId().equals(userId)))
                .orElse(false);
    }
}
