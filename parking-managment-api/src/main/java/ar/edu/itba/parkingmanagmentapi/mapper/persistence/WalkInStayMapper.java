package ar.edu.itba.parkingmanagmentapi.mapper.persistence;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.model.WalkInStay;
import org.springframework.stereotype.Component;

@Component
public class WalkInStayMapper {

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

    public WalkInStay toEntity(Reservation reservation) {
        WalkInStay entity = new WalkInStay();
        entity.setId(reservation.getId());
        entity.setCheckInTime(reservation.getRange().getStart());
        entity.setExpectedEndTime(reservation.getRange().getEnd());
        entity.setStatus(reservation.getStatus());
        entity.setTotalPrice(reservation.getPrice());
        return entity;
    }
}
