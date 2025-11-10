package ar.edu.itba.parkingmanagmentapi.service.orchestrator;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.service.UserVehicleAssignmentService;
import ar.edu.itba.parkingmanagmentapi.service.VehicleService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignVehicleOrchestratorService {
    private final VehicleService vehicleService;
    private final UserVehicleAssignmentService userVehicleAssignmentService;

    public VehicleDomain assignVehicleToUser(VehicleDomain request) {
        if (Objects.nonNull(userVehicleAssignmentService.findByUserIdAndLicensePlate(request.userId(), request.licensePlate()))) {
            throw new BadRequestException("vehicle.already.exists", request.licensePlate());
        }
        var assignment = userVehicleAssignmentService.create(request.userId(), request.licensePlate());

        return vehicleService.create(request, assignment);
    }

    public VehicleDomain updateVehicleForUser(String licensePlate, VehicleDomain request) {
        var toReturn = vehicleService.findByLicensePlate(licensePlate);
        if (toReturn != null) {
            toReturn = vehicleService.update(licensePlate, request);
        }
        return toReturn;
    }

    public VehicleDomain findByLicensePlate(String licensePlate) {
        return vehicleService.findByLicensePlate(licensePlate);
    }

    public void deleteVehicleForUser(String licensePlate) {
        vehicleService.delete(licensePlate);
    }

}
