package com.hiveform.dto.submission;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSummaryResponse {
    private String formId;
    private String formTitle;
    private Long totalSubmissions;
    private List<QuestionSummary> questionSummaries;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionSummary {
        private String questionId;
        private String questionTitle;
        private String questionType;
        private Integer questionIndex;
        private Long totalAnswers;
        
        // For text questions (SHORT_TEXT, LONG_TEXT, EMAIL, URL, NUMBER)
        private List<TextAnswerSample> textAnswerSamples;
        private Long textAnswerCount;
        
        // For choice questions (SINGLE_CHOICE, MULTIPLE_CHOICE, DROPDOWN)
        private List<ChoiceOptionStats> choiceOptionStats;
        
        // For rating questions
        private Double averageRating;
        private Map<Integer, Long> ratingDistribution; // rating -> count
        private Long ratingCount;
        
        // For date/time questions
        private List<DateTimeAnswerSample> dateTimeAnswerSamples;
        private Long dateTimeAnswerCount;
        
        // For file upload questions
        private Long fileUploadCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TextAnswerSample {
        private String answerText;
        private Long count;
        private String percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChoiceOptionStats {
        private String option;
        private Long count;
        private String percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DateTimeAnswerSample {
        private String dateTimeValue;
        private Long count;
        private String percentage;
    }
} 