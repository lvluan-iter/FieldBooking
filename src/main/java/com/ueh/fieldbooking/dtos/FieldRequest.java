package com.ueh.fieldbooking.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class FieldRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String type;

    @NotNull
    @Min(0)
    private BigDecimal pricePerHour;
}
