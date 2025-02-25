package com.ueh.fieldbooking.services;

import com.ueh.fieldbooking.dtos.ScheduleResponse;
import com.ueh.fieldbooking.models.*;
import com.ueh.fieldbooking.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final FieldRepository fieldRepository;
    private final WHourRepository wHourRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ModelMapper mapper;

    public List<ScheduleResponse> getAvailableTimesForFieldAndDate(Long id, LocalDate date) {
        List<Schedule> schedules = scheduleRepository.findByFieldAndDate(id, date);
        return schedules.stream().map(schedule -> mapper.map(schedule, ScheduleResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeScheduleByDate(LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Booking> bookingList;
        List<User> updatedUsers = new ArrayList<>();
        List<Booking> updatedBookings = new ArrayList<>();

        if (date.equals(today)) {
            scheduleRepository.deleteByDateAfterNow(today, now);
            if (now.isAfter(startTime)) {
                startTime = now.withMinute(0).plusHours(1);
            }
            bookingList = bookingRepository.findByDateAfterNow(today, now);
        } else {
            scheduleRepository.deleteByDate(date);
            bookingList = bookingRepository.findByDate(date);
        }

        bookingList.forEach(booking -> {
            booking.setStatus(Booking.Status.CANCELLED);
            updatedBookings.add(booking);

            User user = booking.getCustomer();
            Integer refundPoints = booking.getTotalPrice().divide(BigDecimal.valueOf(1000), RoundingMode.FLOOR).intValue();
            user.setScore(user.getScore() + refundPoints);
            updatedUsers.add(user);

            emailService.sendCancelBookingEmail(
                    user.getEmail(),
                    user.getFullName(),
                    refundPoints,
                    booking.getBookingDate()
            );
        });

        bookingRepository.saveAll(updatedBookings);
        userRepository.saveAll(updatedUsers);

        List<Field> fieldList = fieldRepository.findAll();
        for (Field field:fieldList) {
            createSchedule(startTime,endTime,date,field);
        }
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void automaticSchedule() {
        List<Field> fieldList = fieldRepository.findAll();
        List<WorkHour> workHourList = wHourRepository.findAll();

        if (workHourList.size() < 7) {
            log.warn("⚠️ Lịch làm việc không đủ 7 ngày, vui lòng kiểm tra!");
            return;
        }

        fieldList.forEach(field -> {
            for (int i = 1; i <= 7; i++) {
                LocalDate date = LocalDate.now().plusDays(i);
                WorkHour workHour = workHourList.get(i - 1);
                createSchedule(workHour.getStartTime(), workHour.getEndTime(), date, field);
            }
        });
    }

    @Transactional
    @Scheduled(cron = "0 0 22 * * *")
    public void autoDeleteSchedule() {
        LocalDate today = LocalDate.now();
        int removeCount = scheduleRepository.deleteByDate(today);
        log.info("✅ Đã xóa {} lịch cũ của ngày: {}", removeCount, today);
    }

    private void createSchedule(LocalTime start, LocalTime end, LocalDate date, Field field) {
        List<Schedule> schedules = new ArrayList<>();

        for (LocalTime currentTime = start; !currentTime.plusMinutes(90).isAfter(end); currentTime = currentTime.plusMinutes(90)) {
            Schedule schedule = Schedule.builder()
                    .field(field)
                    .date(date)
                    .startTime(currentTime)
                    .endTime(currentTime.plusMinutes(90))
                    .build();
            schedules.add(schedule);
        }

        scheduleRepository.saveAll(schedules);
    }
}