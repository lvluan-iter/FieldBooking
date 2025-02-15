package com.ueh.fieldbooking.dtos;

import lombok.Setter;

import java.time.LocalDateTime;

@Setter
public class UserResponse {
    private String email;
    private String fullName;
    private String phone;
    private LocalDateTime createdAt;
}
