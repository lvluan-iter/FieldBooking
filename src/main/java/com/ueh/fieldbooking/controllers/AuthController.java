package com.ueh.fieldbooking.controllers;

import com.ueh.fieldbooking.components.JwtAuthenticationFilter;
import com.ueh.fieldbooking.components.JwtProvider;
import com.ueh.fieldbooking.dtos.*;
import com.ueh.fieldbooking.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok(new JwtResponse(jwtProvider.generateToken(authentication)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> userRegister(@RequestBody @Valid RegisterRequest request) {
        userService.userRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(201, "Register successful!"));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);

        if (jwt == null || !jwtProvider.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(401, "Invalid token!"));
        }

        String username = jwtProvider.getUsernameFromToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newToken = jwtProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtResponse(newToken));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> resetPasswordRequest(@RequestBody @Valid ForgotPasswordRequest request) {
        try {
            userService.resetPasswordRequest(request.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200, "Reset password email sent successfully"));
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Failed to send reset password email"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200, "Reset password successful!"));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(HttpServletRequest request, @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        String email = jwtProvider.getUsernameFromToken(jwtAuthenticationFilter.getJwtFromRequest(request));
        userService.changePassword(email, changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200, "Change password successful!"));
    }
}