package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;

import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.model.Spot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class SpotDomain {

    private Long id;
    private VehicleType vehicleType;
    private Integer floor;
    private String code;
    private Boolean isAvailable;
    private Boolean isAccessible;
    private Boolean isReservable;
    private Long parkingLotId;

    public Spot toEntity(ParkingLot parkingLot){
        Spot spot = new Spot();
        if(id != null)
            spot.setId(id);
        spot.setVehicleType(vehicleType);
        spot.setFloor(floor);
        spot.setCode(code);
        spot.setIsAccessible(isAccessible);
        spot.setIsAvailable(isAvailable);
        spot.setIsReservable(isReservable);
        spot.setParkingLot(parkingLot);
        return spot;
    }

    public static SpotDomain fromEntity(Spot spot){
        return new SpotDomain(
                spot.getId(),
                spot.getVehicleType(),
                spot.getFloor(),
                spot.getCode(),
                spot.getIsAvailable(),
                spot.getIsAccessible(),
                spot.getIsReservable(),
                spot.getParkingLot().getId());
    }

}
