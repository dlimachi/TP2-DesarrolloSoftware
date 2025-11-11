package ar.edu.itba.parkingmanagmentapi.util;

import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.query.SortDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ParkingPriceFilter {
    private final BigDecimal min;
    private final BigDecimal max;
    private final VehicleType vehicleType;
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final String sort;
}

