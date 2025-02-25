package com.ueh.fieldbooking.controllers;

import com.ueh.fieldbooking.dtos.ApiResponse;
import com.ueh.fieldbooking.dtos.WHourDTO;
import com.ueh.fieldbooking.services.WHourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/work-hour")
public class WHourController {
    private final WHourService wHourService;

    @GetMapping
    public ResponseEntity<?> getAllWHours() {
        List<WHourDTO> wHourDTOS = wHourService.getAllWorkHours();
        return ResponseEntity.ok(wHourDTOS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWHour(@PathVariable("id") Long id, @RequestBody @Valid WHourDTO dto) {
        wHourService.updateWHour(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200, "Update work hour successful!"));
    }
}
