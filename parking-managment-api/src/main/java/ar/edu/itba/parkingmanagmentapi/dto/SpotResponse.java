package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.SpotDomain;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotResponse {
    private Long id;
    private String vehicleType;
    private Integer floor;
    private String code;
    private Boolean isAvailable;
    private Boolean isReservable;
    private Boolean isAccessible;

    public static SpotResponse fromDomain(SpotDomain spotDomain) {
        return SpotResponse.builder()
                .vehicleType(spotDomain.getVehicleType().getName())
                .floor(spotDomain.getFloor())
                .code(spotDomain.getCode())
                .isAccessible(spotDomain.getIsAccessible())
                .isReservable(spotDomain.getIsReservable())
                .isAvailable(spotDomain.getIsAvailable())
                .id(spotDomain.getId())
                .build();
    }
}
