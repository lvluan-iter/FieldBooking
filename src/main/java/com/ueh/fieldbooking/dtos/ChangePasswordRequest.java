package com.ueh.fieldbooking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    @NotBlank
    @Size(min = 6)
    private String olaPassword;

    @NotBlank
    @Size(min = 6)
    private String newPassword;
}
