package com.ueh.fieldbooking.dtos;

import com.ueh.fieldbooking.models.Type;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
public class FieldResponse {
    private Long id;
    private String name;
    private String address;
    private TypeResponse type;
    private BigDecimal pricePerHour;
    private LocalDateTime createdAt;
}
