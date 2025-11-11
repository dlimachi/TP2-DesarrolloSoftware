package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingPriceDomain;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingPriceResponse {
    private Long id;
    private String vehicleType;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validTo;

    public static ParkingPriceResponse fromDomain(ParkingPriceDomain domain) {
        return new ParkingPriceResponse(
                domain.getId(),
                domain.getVehicleType().getName(),
                domain.getPrice(),
                domain.getValidFrom(),
                domain.getValidTo()
        );
    }

}
