package com.ueh.fieldbooking.dtos;

import com.ueh.fieldbooking.models.Field;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
public class ScheduleResponse {
    private Long id;
    private Field field;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
