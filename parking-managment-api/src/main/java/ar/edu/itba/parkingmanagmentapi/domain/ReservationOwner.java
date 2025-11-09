package ar.edu.itba.parkingmanagmentapi.domain;

import lombok.Value;

@Value
public class ReservationOwner {

    Long userId;
    String licensePlate;

    public static ReservationOwner from(Long userId, String licensePlate) {
        return new ReservationOwner(userId, licensePlate);
    }

}
