package com.hiveform.services;

import com.hiveform.dto.submission.SubmissionRequest;
import com.hiveform.dto.submission.SubmissionResponse;
import com.hiveform.dto.submission.SubmissionListResponse;
import com.hiveform.dto.submission.GetSubmissionsRequest;
import com.hiveform.dto.submission.DeleteSubmissionRequest;

public interface ISubmissionService {
    SubmissionResponse createSubmission(SubmissionRequest submissionRequest, String userId);
    SubmissionResponse getSubmissionById(String submissionId);
    SubmissionListResponse getSubmissionsByFormId(GetSubmissionsRequest request);
    SubmissionListResponse getUserFormSubmissions(GetSubmissionsRequest request);
    void deleteSubmission(DeleteSubmissionRequest request);
}
