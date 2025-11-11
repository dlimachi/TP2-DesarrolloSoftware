package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.VehicleDomain;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleResponse {
    private String licensePlate;
    private String brand;
    private String model;
    private String type;

    public static VehicleResponse fromDomain(VehicleDomain domain) {
        return VehicleResponse.builder()
                .brand(domain.brand())
                .licensePlate(domain.licensePlate())
                .model(domain.model())
                .type(domain.type())
                .build();
    }
}
