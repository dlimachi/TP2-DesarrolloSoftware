package ar.edu.itba.parkingmanagmentapi.mapper.web;

import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.dto.ReservationResponse;
import ar.edu.itba.parkingmanagmentapi.dto.ScheduledReservationRequest;
import org.springframework.stereotype.Component;

@Component("scheduledReservationWebMapper")
public class ScheduledReservationMapper {

    public Reservation toDomain(ScheduledReservationRequest request) {
        return Reservation.builder()
                .userId(request.getUserId())
                .spotId(request.getSpotId())
                .vehicleLicensePlate(request.getVehicleLicensePlate())
                .range(new DateTimeRange(request.getReservedStartTime(), request.getExpectedEndTime()))
                .build();
    }

    public ReservationResponse toDTO(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setReservedStartTime(reservation.getRange().getStart());
        response.setExpectedEndTime(reservation.getRange().getEnd());
        response.setStatus(reservation.getStatus());
        response.setPrice(reservation.getPrice());
        response.setSpotId(reservation.getSpotId());
        response.setVehicleLicensePlate(reservation.getVehicleLicensePlate());
        response.setUserId(reservation.getUserId());
        return response;
    }

}
