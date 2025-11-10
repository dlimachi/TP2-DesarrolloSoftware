package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleRequest {
    private String licensePlate;
    private String brand;
    private String model;
    private String type;
    private Long userId;

    public VehicleDomain toDomain() {
        return new VehicleDomain(
                this.licensePlate,
                this.brand,
                this.model,
                this.type,
                this.userId);
    }
}
