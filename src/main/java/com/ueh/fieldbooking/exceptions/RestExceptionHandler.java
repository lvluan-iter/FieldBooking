package com.ueh.fieldbooking.exceptions;

import com.ueh.fieldbooking.dtos.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiResponse> handIllegalArgumentException(IllegalArgumentException e) {
        ApiResponse errorResponse = new ApiResponse(400, e.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }


}
