package com.ueh.fieldbooking.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ueh.fieldbooking.exceptions.ResourceNotFoundException;
import com.ueh.fieldbooking.models.Field;
import com.ueh.fieldbooking.models.FieldImage;
import com.ueh.fieldbooking.repositories.FImageRepository;
import com.ueh.fieldbooking.repositories.FieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FImageService {
    private final FImageRepository fImageRepository;
    private final FieldRepository fieldRepository;
    private final Cloudinary cloudinary;

    public void addImages(Long fieldId, MultipartFile[] files) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with ID: " + fieldId));

        List<FieldImage> imageList = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                validateImage(file);
                String imageUrl = uploadToCloudinary(file);

                FieldImage fieldImage = FieldImage.builder()
                        .field(field)
                        .imageUrl(imageUrl)
                        .build();
                imageList.add(fieldImage);
            }
            fImageRepository.saveAll(imageList);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload images: " + e.getMessage());
        }
    }

    public void updateImages(Long fieldId, MultipartFile[] newFiles) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with ID: " + fieldId));

        List<FieldImage> existingImages = fImageRepository.findByField(field);

        try {
            for (FieldImage image : existingImages) {
                deleteFromCloudinary(image.getImageUrl());
                fImageRepository.delete(image);
            }

            List<FieldImage> newImageList = new ArrayList<>();

            for (MultipartFile file : newFiles) {
                validateImage(file);
                String newImageUrl = uploadToCloudinary(file);
                FieldImage newImage = FieldImage.builder()
                        .field(field)
                        .imageUrl(newImageUrl)
                        .build();
                newImageList.add(newImage);
            }

            fImageRepository.saveAll(newImageList);

        } catch (IOException e) {
            throw new RuntimeException("Failed to update images: " + e.getMessage());
        }
    }

    public void deleteImages(List<Long> imageIds) {
        List<FieldImage> images = fImageRepository.findAllById(imageIds);

        try {
            for (FieldImage image : images) {
                deleteFromCloudinary(image.getImageUrl());
            }
            fImageRepository.deleteAll(images);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete images: " + e.getMessage());
        }
    }

    public void deleteImage(Long imageId) {
        FieldImage image = fImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + imageId));

        try {
            deleteFromCloudinary(image.getImageUrl());
            fImageRepository.delete(image);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image: " + e.getMessage());
        }
    }

    public List<String> getImagesByField(Long fieldId) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with ID: " + fieldId));

        List<FieldImage> images = fImageRepository.findByField(field);
        List<String> imageUrls = new ArrayList<>();

        for (FieldImage image : images) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }

    public FieldImage getImageById(Long imageId) {
        return fImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + imageId));
    }

    private String uploadToCloudinary(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "field-booking"
        ));
        return (String) uploadResult.get("secure_url");
    }

    private void deleteFromCloudinary(String imageUrl) {
        try {
            String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
            cloudinary.uploader().destroy("field-booking/" + publicId, ObjectUtils.emptyMap());

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from Cloudinary: " + e.getMessage());
        }
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("The image size exceeds the 5MB limit. Please upload a smaller image.");
        }

        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg")) {
            throw new IllegalArgumentException("Only JPG, JPEG, and PNG formats are supported.");
        }
    }
}