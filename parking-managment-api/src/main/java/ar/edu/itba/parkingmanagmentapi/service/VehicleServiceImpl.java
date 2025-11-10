package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.repositoryDomain.VehicleDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleDomainRepository vehicleRepository;

    @Override
    public VehicleDomain create(VehicleDomain request, UserVehicleAssignment vehicleAssignment) {
        return vehicleRepository.save(request, vehicleAssignment);
    }


    @Override
    public VehicleDomain findByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate);
    }

    @Override
    public List<VehicleDomain> findAllVehiclesByUser(Long id) {
        return vehicleRepository.findAllByUserId(id);
    }

    @Override
    @Transactional
    public VehicleDomain update(VehicleDomain request) {
        return vehicleRepository.update(request);
    }

    @Override
    public void delete(String licensePlate) {
        if (Objects.isNull(findByLicensePlate(licensePlate))) {
            throw new NotFoundException("vehicle.not.found", licensePlate);
        }
        vehicleRepository.deleteById(licensePlate);
    }

    @Override
    public boolean isUserOwnerOfVehicle(Long userId, String licensePlate) {
        return vehicleRepository.findOwnerOfVehicle(userId, licensePlate);
    }
}
