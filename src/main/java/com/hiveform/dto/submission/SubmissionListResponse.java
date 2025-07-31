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
public class SubmissionListResponse {
    private List<SubmissionSummary> submissions;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubmissionSummary {
        private String id;
        private String formId;
        private String formTitle;
        private Long submittedAt;
        private String submissionBy;
        private SubmissionByUser submissionByUser;
        private int answerCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubmissionByUser {
        private String id;
        private String fullName;
        private String email;
    }
} 