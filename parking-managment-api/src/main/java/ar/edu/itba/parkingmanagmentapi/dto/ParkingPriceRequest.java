package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingPriceDomain;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingPriceRequest {
    private String vehicleType;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validTo;

    public static ParkingPriceDomain toDomain(ParkingPriceRequest request) {
        return new ParkingPriceDomain(
                null,
                request.getPrice(),
                VehicleType.fromName(request.getVehicleType()),
                request.getValidFrom(),
                request.getValidTo(),
                null
        );
    }

}
