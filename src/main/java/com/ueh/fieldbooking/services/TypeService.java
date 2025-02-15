package com.ueh.fieldbooking.services;

import com.ueh.fieldbooking.dtos.TypeRequest;
import com.ueh.fieldbooking.dtos.TypeResponse;
import com.ueh.fieldbooking.exceptions.ResourceNotFoundException;
import com.ueh.fieldbooking.models.Type;
import com.ueh.fieldbooking.repositories.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TypeService {
    private final TypeRepository typeRepository;
    private final ModelMapper modelMapper;

    public void addType(TypeRequest request) {
        if (typeRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Type already exists!");
        }

        Type type = Type.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        typeRepository.save(type);
    }

    public void updateType(Long id, TypeRequest request) {
        Type type = typeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type not found with ID: " + id));

        if (!type.getName().equals(request.getName()) && typeRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Type name already exists!");
        }

        type.setName(request.getName());
        type.setDescription(request.getDescription());
        typeRepository.save(type);
    }

    public void deleteType(Long id) {
        if (!typeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Type not found with ID: " + id);
        }
        typeRepository.deleteById(id);
    }

    public List<TypeResponse> getAllTypes() {
        return typeRepository.findAll().stream()
                .map(type -> modelMapper.map(type, TypeResponse.class))
                .collect(Collectors.toList());
    }

    public TypeResponse getType(Long id) {
        Type type = typeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type not found with ID: " + id));
        return modelMapper.map(type, TypeResponse.class);
    }

    public TypeResponse getTypeByName(String name) {
        Type type = typeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Type not found with name: " + name));
        return modelMapper.map(type, TypeResponse.class);
    }
}