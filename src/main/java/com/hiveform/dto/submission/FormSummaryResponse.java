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
        
        private List<TextAnswerSample> textAnswerSamples;
        private Long textAnswerCount;
        
        private List<ChoiceOptionStats> choiceOptionStats;
        
        private Double averageRating;
        private Map<Integer, Long> ratingDistribution; // rating -> count
        private Long ratingCount;
        
        private List<DateTimeAnswerSample> dateTimeAnswerSamples;
        private Long dateTimeAnswerCount;
        
        private Long fileUploadCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TextAnswerSample {
        private String answerText;
        private Long count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChoiceOptionStats {
        private String option;
        private Long count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DateTimeAnswerSample {
        private String dateTimeValue;
        private Long count;
    }
} 