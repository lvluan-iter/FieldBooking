package com.ueh.fieldbooking.controllers;

import com.ueh.fieldbooking.components.JwtAuthenticationFilter;
import com.ueh.fieldbooking.components.JwtProvider;
import com.ueh.fieldbooking.dtos.ApiResponse;
import com.ueh.fieldbooking.dtos.UserRequest;
import com.ueh.fieldbooking.dtos.UserResponse;
import com.ueh.fieldbooking.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtProvider jwtProvider;

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin', 'Owner')")
    public ResponseEntity<?> getAllUsers() {
        List<UserResponse> userResponseList = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(userResponseList);
    }

    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        String email = jwtProvider.getUsernameFromToken(jwtAuthenticationFilter.getJwtFromRequest(request));
        UserResponse userResponse = userService.getUser(email);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @RequestBody @Valid UserRequest userRequest) {
        String email = jwtProvider.getUsernameFromToken(jwtAuthenticationFilter.getJwtFromRequest(request));
        UserResponse response = userService.updateUser(email, userRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        String email = jwtProvider.getUsernameFromToken(jwtAuthenticationFilter.getJwtFromRequest(request));
        userService.deleteUser(email);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200, "Delete user successful!"));
    }
}