package com.hiveform.dto.form;

import java.util.List;

import com.hiveform.dto.question.DtoQuestionIU;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class DtoFormIU {
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
    
    private Long expiresAt;
    
    @Valid
    private List<DtoQuestionIU> questions;

    @JsonIgnore
    private String userId;

    @JsonIgnore
    private String formId;
}
