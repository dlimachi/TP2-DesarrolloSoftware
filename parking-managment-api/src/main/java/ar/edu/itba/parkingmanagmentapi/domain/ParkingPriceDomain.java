package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.model.ParkingPrice;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ParkingPriceDomain {

    private Long id;
    private final BigDecimal price;
    private final VehicleType vehicleType;
    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;
    private ParkingLotDomain parkingLot;

    public ParkingPrice toEntity() {
        ParkingLot parkingLotEntity = parkingLot != null ? parkingLot.toEntity() : null;
        ParkingPrice entity = new ParkingPrice(vehicleType, price, validFrom, validTo, parkingLotEntity);

        if (id != null)
            entity.setId(id);

        return entity;
    }

    public static ParkingPriceDomain fromEntity(ParkingPrice entity) {
        return new ParkingPriceDomain(
                entity.getId(),
                entity.getPrice(),
                entity.getVehicleType(),
                entity.getValidFrom(),
                entity.getValidTo(),
                entity.getParkingLot() != null ? ParkingLotDomain.fromEntity(entity.getParkingLot()) : null
        );
    }

    public void setId(Long id) { this.id = id; }

    public void setParkingLot(ParkingLotDomain parkingLot) { this.parkingLot = parkingLot; }
}
