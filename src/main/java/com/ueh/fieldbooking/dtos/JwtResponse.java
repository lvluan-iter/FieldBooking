package com.ueh.fieldbooking.dtos;

import lombok.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String token;
}
