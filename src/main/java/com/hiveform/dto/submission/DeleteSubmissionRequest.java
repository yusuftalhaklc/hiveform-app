package com.hiveform.dto.submission;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteSubmissionRequest {
    
    @NotBlank(message = "Submission ID is required")
    private String submissionId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
} 