package com.hiveform.services;

import com.hiveform.dto.submission.SubmissionRequest;
import com.hiveform.dto.submission.SubmissionResponse;
import com.hiveform.dto.submission.SubmissionListResponse;

public interface ISubmissionService {
    SubmissionResponse createSubmission(SubmissionRequest submissionRequest, String userId);
    SubmissionResponse getSubmissionById(String submissionId);
    SubmissionListResponse getSubmissionsByFormId(String formId, int page, int size);
    SubmissionListResponse getUserFormSubmissions(String formId, String userId, int page, int size);
    void deleteSubmission(String submissionId, String userId);
}
