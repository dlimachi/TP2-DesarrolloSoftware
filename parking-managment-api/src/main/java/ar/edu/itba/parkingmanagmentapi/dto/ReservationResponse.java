package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.Reservation;
import ar.edu.itba.parkingmanagmentapi.dto.enums.ReservationStatus;
import ar.edu.itba.parkingmanagmentapi.model.ScheduledReservation;
import ar.edu.itba.parkingmanagmentapi.model.WalkInStay;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservedStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectedEndTime;
    private ReservationStatus status;
    private BigDecimal price;
    private Long spotId;
    private String vehicleLicensePlate;
    private Long userId;

    private String spotName;
    private String userName;
    private String userLastName;
    private String vehicleInfo;
    private String type;

    public static ReservationResponse fromDomain(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservedStartTime(reservation.getRange().getStart())
                .expectedEndTime(reservation.getRange().getEnd())
                .status(reservation.getStatus())
                .price(reservation.getPrice())
                .spotId(reservation.getSpotId())
                .vehicleLicensePlate(reservation.getVehicleLicensePlate())
                .userId(reservation.getUserId())
                .build();
    }

}
