package ar.edu.itba.parkingmanagmentapi.dto.enums;

public enum VehicleType {
    BICYCLE("bicicleta"),
    CAR("auto"),
    VAN("camioneta"),
    MOTORCYCLE("moto");

    private final String name;

    VehicleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static VehicleType fromName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("VehicleType name cannot be null or blank");
        }
        for (VehicleType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No VehicleType found with name: " + name);
    }
}
