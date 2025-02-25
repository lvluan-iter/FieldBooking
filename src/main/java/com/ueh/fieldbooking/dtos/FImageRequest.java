package com.ueh.fieldbooking.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class FImageRequest {
    @NotNull
    private Long fieldId;

    @NotNull
    private MultipartFile[] files;
}
