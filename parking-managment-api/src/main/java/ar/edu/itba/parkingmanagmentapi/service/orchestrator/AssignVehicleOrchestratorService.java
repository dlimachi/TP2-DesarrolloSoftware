package ar.edu.itba.parkingmanagmentapi.service.orchestrator;

import ar.edu.itba.parkingmanagmentapi.dto.VehicleRequest;
import ar.edu.itba.parkingmanagmentapi.dto.VehicleResponse;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.model.UserVehicleAssignment;
import ar.edu.itba.parkingmanagmentapi.model.Vehicle;
import ar.edu.itba.parkingmanagmentapi.service.UserService;
import ar.edu.itba.parkingmanagmentapi.service.UserVehicleAssignmentService;
import ar.edu.itba.parkingmanagmentapi.service.VehicleService;
import ar.edu.itba.parkingmanagmentapi.util.VehicleMapper;
import ar.edu.itba.parkingmanagmentapi.validators.CreateVehicleValidator;
import ar.edu.itba.parkingmanagmentapi.validators.UpdateVehicleValidator;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignVehicleOrchestratorService {
    private final VehicleService vehicleService;
    private final UserVehicleAssignmentService userVehicleAssignmentService;
    private final UserService userService;
    private final CreateVehicleValidator createVehicleValidator;
    private final UpdateVehicleValidator updateVehicleValidator;

    public VehicleResponse assignVehicleToUser(VehicleRequest request) {
        createVehicleValidator.validate(request);

        User user = userService.findById(request.getUserId());
        UserVehicleAssignment assignment = userVehicleAssignmentService.findByUserIdAndLicensePlate(request.getUserId(), request.getLicensePlate());
        if (Objects.nonNull(assignment)) {
            throw new BadRequestException("vehicle.already.exists", request.getLicensePlate());
        }

        Vehicle vehicle = VehicleMapper.toEntity(request);
        vehicle.getUserAssignments().add(new UserVehicleAssignment(user, vehicle));

        return VehicleMapper.toResponse(vehicleService.create(vehicle));
    }

    public VehicleResponse updateVehicleForUser(String licensePlate, VehicleRequest request) {
        updateVehicleValidator.validate(request);

        return VehicleMapper.toResponse(vehicleService.update(licensePlate, VehicleMapper.toEntity(request)));
    }

    public VehicleResponse findByLicensePlate(String licensePlate) {
        return VehicleMapper.toResponse(vehicleService.findByLicensePlate(licensePlate));
    }

    public void deleteVehicleForUser(String licensePlate) {
        vehicleService.delete(licensePlate);
    }

}
