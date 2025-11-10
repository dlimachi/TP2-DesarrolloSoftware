package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.model.Vehicle;

public record VehicleDomain(
        String licensePlate,
        String brand,
        String model,
        String type,
        Long userId
) {

    public Vehicle toEntity() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(this.licensePlate);
        vehicle.setBrand(this.brand);
        vehicle.setModel(this.model);
        vehicle.setType(this.type);
        return vehicle;
    }

    public static VehicleDomain fromEntity(Vehicle vehicle) {
        return new VehicleDomain(
                vehicle.getLicensePlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getType(),
                vehicle.getUserAssignments() != null && !vehicle.getUserAssignments().isEmpty()
                        ? vehicle.getUserAssignments().get(0).getUser().getId()
                        : null
        );
    }
}

