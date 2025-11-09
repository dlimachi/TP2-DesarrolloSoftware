package ar.edu.itba.parkingmanagmentapi.mapper.persistence;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import org.springframework.stereotype.Component;

@Component
public class ScheduledReservationMapper {

    public Reservation toDomain(ScheduledReservation entity) {
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

    public ScheduledReservation toEntity(Reservation reservation) {
        ScheduledReservation entity = new ScheduledReservation();
        entity.setId(reservation.getId());
        entity.setReservedStartTime(reservation.getRange().getStart());
        entity.setExpectedEndTime(reservation.getRange().getEnd());
        entity.setStatus(reservation.getStatus());
        entity.setEstimatedPrice(reservation.getPrice());
        return entity;
    }
}
