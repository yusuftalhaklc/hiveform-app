package com.hiveform.services;

import com.hiveform.dto.submission.SubmissionRequest;
import com.hiveform.dto.submission.SubmissionResponse;
import com.hiveform.dto.submission.SubmissionListResponse;
import com.hiveform.dto.submission.GetSubmissionsRequest;
import com.hiveform.dto.submission.DeleteSubmissionRequest;
import com.hiveform.dto.submission.GetSubmissionByIdRequest;
import com.hiveform.dto.submission.FormSummaryResponse;
import com.hiveform.dto.submission.QuestionDetailResponse;
import com.hiveform.dto.submission.GetFormSummaryRequest;
import com.hiveform.dto.submission.GetQuestionDetailRequest;

public interface ISubmissionService {
    SubmissionResponse createSubmission(SubmissionRequest submissionRequest, String userId);
    SubmissionResponse getSubmissionById(GetSubmissionByIdRequest request);
    SubmissionListResponse getSubmissionsByFormId(GetSubmissionsRequest request);
    SubmissionListResponse getUserFormSubmissions(GetSubmissionsRequest request);
    void deleteSubmission(DeleteSubmissionRequest request);
    
    // New methods for form owner views
    FormSummaryResponse getFormSummary(GetFormSummaryRequest request);
    QuestionDetailResponse getQuestionDetail(GetQuestionDetailRequest request);
}
