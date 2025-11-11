package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotRequest {
    private String vehicleType;

    private Integer floor;

    private String code;

    private Boolean isAvailable = true;

    private Boolean isReservable;

    private Boolean isAccessible;

    public SpotDomain toDomain(SpotRequest spotRequest) {
        VehicleType vehicleType = VehicleType.fromName(spotRequest.getVehicleType());
        // Asumo que en el dominio, un ID en cero se interpreta como no asignado.
        // TODO: Al crear el Spot como manejo el ParkingLot? pongo solo el ID en 0 o la entidad y le pongo null al principio?
        return new SpotDomain(0L,
                vehicleType,
                spotRequest.getFloor(),
                spotRequest.getCode(),
                spotRequest.getIsAvailable(),
                spotRequest.getIsReservable(),
                spotRequest.getIsAccessible(),
                null);
    }
}