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
        return new SpotDomain(null,
                vehicleType,
                spotRequest.getFloor(),
                spotRequest.getCode(),
                spotRequest.getIsAvailable(),
                spotRequest.getIsAccessible(),
                spotRequest.getIsReservable(),
                null);
    }

    public SpotDomain toDomainWithId(Long id, SpotRequest spotRequest){
        SpotDomain spotDomain = toDomain(spotRequest);
        spotDomain.setId(id);
        return spotDomain;
    }
}