package com.hiveform.dto.form;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoFormUpdate {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private String bannerImageUrl;

    @NotNull(message = "Is active status is required")
    private Boolean isActive;

    @NotNull(message = "Is public status is required")
    private Boolean isPublic;

    private LocalDateTime expiresAt;

    @JsonIgnore
    private String formId;

    @JsonIgnore
    private String userId;

} 