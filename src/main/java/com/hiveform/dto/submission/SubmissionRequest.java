package com.hiveform.dto.submission;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionRequest {
    
    @NotEmpty(message = "At least one answer is required")
    @Valid
    private List<AnswerRequest> answers;

    @JsonIgnore
    private String formId;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerRequest {
        @NotBlank(message = "Question ID is required")
        private String questionId;
        
        private String answerText;
        private String selectedOption;
        private List<String> selectedOptions;
        private String fileUrl;
        private String selectedDate;
        private String selectedTime;
        private Integer selectedRating;
    }
} 