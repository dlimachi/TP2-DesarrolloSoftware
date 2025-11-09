package ar.edu.itba.parkingmanagmentapi.domain;


import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Reservation {

    Long id;
    Long userId;
    Long spotId;
    String vehicleLicensePlate;
    ReservationStatus status;
    BigDecimal price;
    DateTimeRange range;

}
