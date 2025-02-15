package com.ueh.fieldbooking.services;

import com.ueh.fieldbooking.dtos.*;
import com.ueh.fieldbooking.exceptions.ResourceNotFoundException;
import com.ueh.fieldbooking.models.User;
import com.ueh.fieldbooking.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public void userRegister(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("The email already exists!");
        }

        if (isInvalidPassword(request.getPassword())) {
            throw new RuntimeException("Password does not meet complexity requirements");
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(encoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
    }

    public void resetPasswordRequest(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with " + email));
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setTokenCreationDate(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendResetPasswordEmail(email, user.getFullName(), token);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with" + request.getToken()));
        if (user.getTokenCreationDate().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }
        user.setPassword(encoder.encode(request.getPassword()));
        user.setResetPasswordToken(null);
        user.setTokenCreationDate(null);
        userRepository.save(user);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (isInvalidPassword(request.getNewPassword())) {
            throw new IllegalArgumentException("New password does not meet complexity requirements");
        }

        if (!encoder.matches(request.getOlaPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password does not meet");
        }

        if (encoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the old password");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private boolean isInvalidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return !password.matches(passwordRegex);
    }

    public List<UserResponse> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    public UserResponse getUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with : " + email));
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse updateUser(String email, UserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with : " + email));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with : " + email));
        userRepository.delete(user);
    }
}
