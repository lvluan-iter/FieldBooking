package com.ueh.fieldbooking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TypeRequest {
    @NotBlank
    @Size(min = 3, max = 150)
    private String name;
    private String description;
}
