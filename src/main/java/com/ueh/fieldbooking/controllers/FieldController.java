package com.ueh.fieldbooking.controllers;

import com.ueh.fieldbooking.dtos.ApiResponse;
import com.ueh.fieldbooking.dtos.FieldRequest;
import com.ueh.fieldbooking.dtos.FieldResponse;
import com.ueh.fieldbooking.services.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fields")
public class FieldController {
    private final FieldService fieldService;

    @PostMapping
    public ResponseEntity<ApiResponse> addField(@RequestBody FieldRequest request) {
        fieldService.addField(request);
        return ResponseEntity.status(201).body(new ApiResponse(201, "Field added successfully!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateField(@PathVariable Long id, @RequestBody FieldRequest request) {
        fieldService.updateField(id, request);
        return ResponseEntity.ok(new ApiResponse(200, "Field updated successfully!"));
    }

    @GetMapping
    public ResponseEntity<List<FieldResponse>> getAllFields() {
        return ResponseEntity.ok(fieldService.getAllFields());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldResponse> getFieldById(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.getFieldById(id));
    }

    @GetMapping("/type/{typeName}")
    public ResponseEntity<List<FieldResponse>> getFieldsByType(@PathVariable String typeName) {
        return ResponseEntity.ok(fieldService.getFieldsByType(typeName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteField(@PathVariable Long id) {
        fieldService.deleteField(id);
        return ResponseEntity.ok(new ApiResponse(200, "Field deleted successfully!"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchField(@RequestParam("key") String key) {
        List<FieldResponse> responses = fieldService.searchField(key);
        return ResponseEntity.ok(responses);
    }
}
