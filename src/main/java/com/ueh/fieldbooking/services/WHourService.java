package com.ueh.fieldbooking.services;

import com.ueh.fieldbooking.dtos.WHourDTO;
import com.ueh.fieldbooking.exceptions.ResourceNotFoundException;
import com.ueh.fieldbooking.models.WorkHour;
import com.ueh.fieldbooking.repositories.WHourRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WHourService {
    private final WHourRepository wHourRepository;
    private final ModelMapper modelMapper;
    private final ScheduleService scheduleService;

    @Transactional
    public void updateWHour(Long id, WHourDTO dto) {
        WorkHour workHour = wHourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work hour not found with : " +dto.getDayOfWeek()));
        workHour.setStartTime(dto.getStartTime());
        workHour.setEndTime(dto.getEndTime());
        wHourRepository.save(workHour);

        scheduleService.changeScheduleByDate(dto.getDate(), dto.getStartTime(), dto.getEndTime());
    }

    public List<WHourDTO> getAllWorkHours() {
        List<WorkHour> list = wHourRepository.findAll();
        return list.stream().map(workHour -> modelMapper.map(workHour, WHourDTO.class))
                .collect(Collectors.toList());
    }
}
