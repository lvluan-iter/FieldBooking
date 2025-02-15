package com.ueh.fieldbooking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
}
