package com.ueh.fieldbooking.controllers;

import com.ueh.fieldbooking.dtos.ScheduleResponse;
import com.ueh.fieldbooking.services.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<?> findScheduleByFieldAndDate(@RequestParam("id") Long id,
                                                        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ScheduleResponse> responses = scheduleService.getAvailableTimesForFieldAndDate(id, date);
        return ResponseEntity.ok(responses);
    }
}
