package com.ueh.fieldbooking.dtos;

import com.ueh.fieldbooking.models.Field;
import com.ueh.fieldbooking.models.User;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class BookingDTO {
    private Long id;
    private User customer;
    private Field field;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal totalPrice;
    private String status;
}
