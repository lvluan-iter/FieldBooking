package com.ueh.fieldbooking.controllers;

import com.ueh.fieldbooking.dtos.ApiResponse;
import com.ueh.fieldbooking.services.FImageService;
import com.ueh.fieldbooking.models.FieldImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/fields/{fieldId}/images")
@RequiredArgsConstructor
public class FImageController {
    private final FImageService fImageService;

    @PostMapping
    public ResponseEntity<?> uploadImages(
            @PathVariable Long fieldId,
            @RequestParam("files") MultipartFile[] files) {
        fImageService.addImages(fieldId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(201,"Images uploaded successfully."));
    }

    @PutMapping
    public ResponseEntity<?> updateImages(
            @PathVariable Long fieldId,
            @RequestParam("files") MultipartFile[] newFiles) {
        fImageService.updateImages(fieldId, newFiles);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200,"Images updated successfully."));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        fImageService.deleteImage(imageId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200,"Image deleted successfully."));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMultipleImages(@RequestBody List<Long> imageIds) {
        fImageService.deleteImages(imageIds);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200,"Selected images deleted successfully."));
    }

    @GetMapping
    public ResponseEntity<List<String>> getImages(@PathVariable Long fieldId) {
        List<String> imageUrls = fImageService.getImagesByField(fieldId);
        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<FieldImage> getImage(@PathVariable Long imageId) {
        FieldImage image = fImageService.getImageById(imageId);
        return ResponseEntity.ok(image);
    }
}
