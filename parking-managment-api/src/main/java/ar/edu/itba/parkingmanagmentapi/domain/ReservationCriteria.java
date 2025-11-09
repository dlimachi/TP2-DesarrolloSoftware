package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReservationCriteria {

    Long userId;
    Long parkingLotId;
    String licensePlate;
    ReservationStatus status;
    DateTimeRange range;


}
