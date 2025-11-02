package ar.edu.itba.parkingmanagmentapi.domain;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class DateTimeRange {
    LocalDateTime start;
    LocalDateTime end;

    public static DateTimeRange from(LocalDateTime start, LocalDateTime end) {
        return new DateTimeRange(start, end);
    }
}