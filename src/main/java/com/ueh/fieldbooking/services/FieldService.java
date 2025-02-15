package com.ueh.fieldbooking.services;

import com.ueh.fieldbooking.dtos.FieldRequest;
import com.ueh.fieldbooking.dtos.FieldResponse;
import com.ueh.fieldbooking.dtos.TypeResponse;
import com.ueh.fieldbooking.exceptions.ResourceNotFoundException;
import com.ueh.fieldbooking.models.Field;
import com.ueh.fieldbooking.models.Type;
import com.ueh.fieldbooking.repositories.FieldRepository;
import com.ueh.fieldbooking.repositories.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FieldService {
    private final FieldRepository fieldRepository;
    private final TypeRepository typeRepository;
    private final ModelMapper modelMapper;

    public void addField(FieldRequest request) {
        Type type = typeRepository.findByName(request.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Type not found with: " + request.getType()));

        if (fieldRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("The field already exists!");
        }

        Field field = Field.builder()
                .name(request.getName())
                .address(request.getAddress())
                .pricePerHour(request.getPricePerHour())
                .type(type)
                .build();

        fieldRepository.save(field);
    }

    public void updateField(Long id, FieldRequest request) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));

        Type type = typeRepository.findByName(request.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Type not found with: " + request.getType()));

        fieldRepository.findByName(request.getName()).ifPresent(existingField -> {
            if (!existingField.getId().equals(id)) {
                throw new IllegalArgumentException("The field name already exists!");
            }
        });

        field.setName(request.getName());
        field.setAddress(request.getAddress());
        field.setPricePerHour(request.getPricePerHour());
        field.setType(type);

        fieldRepository.save(field);
    }

    public List<FieldResponse> getAllFields() {
        return fieldRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FieldResponse getFieldById(Long id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));
        return mapToResponse(field);
    }

    public List<FieldResponse> getFieldsByType(String typeName) {
        Type type = typeRepository.findByName(typeName)
                .orElseThrow(() -> new ResourceNotFoundException("Type not found: " + typeName));

        return fieldRepository.findByType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteField(Long id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));
        fieldRepository.delete(field);
    }

    private FieldResponse mapToResponse(Field field) {
        FieldResponse response = modelMapper.map(field, FieldResponse.class);
        response.setType(modelMapper.map(field.getType(), TypeResponse.class));
        return response;
    }
}
