package com.hiveform.dto.question;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {
    @NotBlank(message = "Question title is required")
    @Size(min = 1, max = 500, message = "Question title must be between 1 and 500 characters")
    private String title;
    
    @Size(max = 1000, message = "Question description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Question index is required")
    private Integer questionIndex;
    
    private String imageUrl;
    
    @NotBlank(message = "Question type is required")
    private String type;
    
    @NotNull(message = "Is required status is required")
    private Boolean isRequired;
    
    private List<String> options;
} 