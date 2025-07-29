package com.hiveform.dto.submission;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetQuestionDetailRequest {
    
    @NotBlank(message = "Form ID is required")
    private String formId;
    
    @NotBlank(message = "Question ID is required")
    private String questionId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Page number is required")
    @Min(value = 1, message = "Page number must be at least 1")
    private Integer page;
    
    @NotNull(message = "Page size is required")
    @Min(value = 1, message = "Page size must be at least 1")
    private Integer size;
} 