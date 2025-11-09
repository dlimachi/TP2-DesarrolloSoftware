package ar.edu.itba.parkingmanagmentapi.mapper.web;

import ar.edu.itba.parkingmanagmentapi.config.AppConstants;
import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.dto.ReservationResponse;
import ar.edu.itba.parkingmanagmentapi.dto.WalkInStayRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WalkInStayMapper {

    public Reservation toDomain(WalkInStayRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return Reservation.builder()
                .userId(AppConstants.DEFAULT_USER_ID)
                .spotId(request.getSpotId())
                .vehicleLicensePlate(request.getVehicleLicensePlate())
                .range(new DateTimeRange(now, now.plusHours(request.getExpectedDurationHours())))
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
