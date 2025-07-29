package com.hiveform.dto.submission;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDetailResponse {
    private String questionId;
    private String questionTitle;
    private String questionType;
    private Integer questionIndex;
    private Long totalAnswers;
    private List<QuestionAnswer> answers;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionAnswer {
        private String submissionId;
        private String submissionBy;
        private Long submittedAt;
        private String answerText;
        private String selectedOption;
        private List<String> selectedOptions;
        private String fileUrl;
        private String selectedDate;
        private String selectedTime;
        private Integer selectedRating;
    }
} 