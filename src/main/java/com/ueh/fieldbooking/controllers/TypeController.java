package com.ueh.fieldbooking.controllers;

import com.ueh.fieldbooking.dtos.TypeRequest;
import com.ueh.fieldbooking.dtos.TypeResponse;
import com.ueh.fieldbooking.services.TypeService;
import com.ueh.fieldbooking.dtos.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/types")
public class TypeController {
    private final TypeService typeService;

    @PostMapping
    public ResponseEntity<ApiResponse> addType(@RequestBody TypeRequest request) {
        typeService.addType(request);
        return ResponseEntity.ok(new ApiResponse(201, "Type added successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateType(@PathVariable Long id, @RequestBody TypeRequest request) {
        typeService.updateType(id, request);
        return ResponseEntity.ok(new ApiResponse(200, "Type updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteType(@PathVariable Long id) {
        typeService.deleteType(id);
        return ResponseEntity.ok(new ApiResponse(200, "Type deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<List<TypeResponse>> getAllTypes() {
        return ResponseEntity.ok(typeService.getAllTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeResponse> getTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(typeService.getType(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TypeResponse> getTypeByName(@PathVariable String name) {
        return ResponseEntity.ok(typeService.getTypeByName(name));
    }
}
