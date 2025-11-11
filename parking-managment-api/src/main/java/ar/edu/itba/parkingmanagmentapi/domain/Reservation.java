package ar.edu.itba.parkingmanagmentapi.domain;


import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import ar.edu.itba.parkingmanagmentapi.model.WalkInStay;
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

    public static Reservation toDomain(ScheduledReservation entity) {
        return Reservation.builder()
                .id(entity.getId())
                .userId(entity.getUserVehicleAssignment().getUser().getId())
                .spotId(entity.getSpot() != null ? entity.getSpot().getId() : null)
                .vehicleLicensePlate(entity.getUserVehicleAssignment().getVehicle().getLicensePlate())
                .status(entity.getStatus())
                .price(entity.getEstimatedPrice())
                .range(new DateTimeRange(entity.getReservedStartTime(), entity.getExpectedEndTime()))
                .build();
    }

    public ScheduledReservation toScheduledEntity(Reservation reservation) {
        ScheduledReservation entity = new ScheduledReservation();
        entity.setId(reservation.getId());
        entity.setReservedStartTime(reservation.getRange().getStart());
        entity.setExpectedEndTime(reservation.getRange().getEnd());
        entity.setStatus(reservation.getStatus());
        entity.setEstimatedPrice(reservation.getPrice());
        return entity;
    }

    public Reservation toDomain(WalkInStay entity) {
        return Reservation.builder()
                .id(entity.getId())
                .userId(entity.getUserVehicleAssignment().getUser().getId())
                .spotId(entity.getSpot() != null ? entity.getSpot().getId() : null)
                .vehicleLicensePlate(entity.getUserVehicleAssignment().getVehicle().getLicensePlate())
                .status(entity.getStatus())
                .price(entity.getTotalPrice())
                .range(new DateTimeRange(entity.getCheckInTime(), entity.getExpectedEndTime()))
                .build();
    }

    public WalkInStay toWalkInStayEntity(Reservation reservation) {
        WalkInStay entity = new WalkInStay();
        entity.setId(reservation.getId());
        entity.setCheckInTime(reservation.getRange().getStart());
        entity.setExpectedEndTime(reservation.getRange().getEnd());
        entity.setStatus(reservation.getStatus());
        entity.setTotalPrice(reservation.getPrice());
        return entity;
    }

}
