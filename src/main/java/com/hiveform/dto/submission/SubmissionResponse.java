package com.hiveform.dto.submission;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.hiveform.dto.user.UserInfoResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    private String id;
    private String formId;
    private String formTitle;
    private Long submittedAt;
    private UserInfoResponse submissionBy; 
    private List<AnswerResponse> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerResponse {
        private String id;
        private String questionId;
        private String questionTitle;
        private String questionType;
        private String answerText;
        private String selectedOption;
        private List<String> selectedOptions;
        private String fileUrl;
        private String selectedDate;
        private String selectedTime;
        private Integer selectedRating;
    }
} 