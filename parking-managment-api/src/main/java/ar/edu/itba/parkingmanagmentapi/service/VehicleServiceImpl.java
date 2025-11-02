package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.dto.VehicleResponse;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
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
    public Vehicle create(Vehicle request) {
        return vehicleRepository.save(request);
    }


    @Override
    public Vehicle findByLicensePlate(String licensePlate) {
        return vehicleRepository.findById(licensePlate)
                .orElseThrow(() -> new NotFoundException("vehicle.not.found", licensePlate));
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
    public Vehicle update(String licensePlate, Vehicle request) {
        Vehicle vehicle = findByLicensePlate(licensePlate);

        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setType(request.getType());

        return vehicleRepository.save(request);
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
