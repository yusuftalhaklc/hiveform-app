package com.hiveform.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hiveform.controller.ISubmissionController;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;
import com.hiveform.dto.submission.SubmissionRequest;
import com.hiveform.dto.submission.SubmissionResponse;
import com.hiveform.dto.submission.SubmissionListResponse;
import com.hiveform.services.ISubmissionService;
import com.hiveform.security.JwtClaim;
import com.hiveform.dto.submission.GetSubmissionsRequest;
import com.hiveform.dto.submission.DeleteSubmissionRequest;
import com.hiveform.dto.submission.GetSubmissionByIdRequest;
import com.hiveform.dto.submission.FormSummaryResponse;
import com.hiveform.dto.submission.GetFormSummaryRequest;
import com.hiveform.dto.submission.QuestionDetailResponse;
import com.hiveform.dto.submission.GetQuestionDetailRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/submission")
@Tag(name = "Submission", description = "Form Submission API")
public class SubmissionController implements ISubmissionController {

    @Autowired
    private ISubmissionService submissionService;

    @PostMapping("")
    @Override
    public ResponseEntity<ApiResponse<SubmissionResponse>> createSubmission(
            @Valid @RequestBody SubmissionRequest submissionRequest,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request) {
        
        String userId = null;
        if (jwtClaim != null) {
            userId = jwtClaim.getUserId();
        }
        
        SubmissionResponse response = submissionService.createSubmission(submissionRequest, userId);
        return ResponseEntity.ok(RootResponse.success(response, "Form submitted successfully", request.getRequestURI()));
    }

    @GetMapping("/{submissionId}")
    @Override
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(
            @PathVariable String submissionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request) {
        GetSubmissionByIdRequest getSubmissionRequest = GetSubmissionByIdRequest.builder()
                .submissionId(submissionId)
                .userId(jwtClaim.getUserId())
                .build();
        
        SubmissionResponse response = submissionService.getSubmissionById(getSubmissionRequest);
        return ResponseEntity.ok(RootResponse.success(response, "Submission retrieved successfully", request.getRequestURI()));
    }

    @GetMapping("/form/{formId}")
    @Override
    public ResponseEntity<ApiResponse<SubmissionListResponse>> getSubmissionsByFormId(
            @PathVariable String formId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        GetSubmissionsRequest getSubmissionsRequest = GetSubmissionsRequest.builder()
                .formId(formId)
                .userId(jwtClaim.getUserId())
                .page(page)
                .size(size)
                .build();
        
        SubmissionListResponse response = submissionService.getUserFormSubmissions(getSubmissionsRequest);
        return ResponseEntity.ok(RootResponse.success(response, "Form submissions retrieved successfully", request.getRequestURI()));
    }

    @DeleteMapping("/{submissionId}")
    @Override
    public ResponseEntity<ApiResponse<Void>> deleteSubmission(
            @PathVariable String submissionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request) {
        
        DeleteSubmissionRequest deleteRequest = DeleteSubmissionRequest.builder()
                .submissionId(submissionId)
                .userId(jwtClaim.getUserId())
                .build();
        
        submissionService.deleteSubmission(deleteRequest);
        return ResponseEntity.ok(RootResponse.success(null, "Submission deleted successfully", request.getRequestURI()));
    }
    
    // New endpoints for form owner views
    @GetMapping("/form/{formId}/summary")
    @Override
    public ResponseEntity<ApiResponse<FormSummaryResponse>> getFormSummary(
            @PathVariable String formId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        GetFormSummaryRequest getFormSummaryRequest = GetFormSummaryRequest.builder()
                .formId(formId)
                .userId(jwtClaim.getUserId())
                .page(page)
                .size(size)
                .build();
        
        FormSummaryResponse response = submissionService.getFormSummary(getFormSummaryRequest);
        return ResponseEntity.ok(RootResponse.success(response, "Form summary retrieved successfully", request.getRequestURI()));
    }
    
    @GetMapping("/form/{formId}/question/{questionId}")
    @Override
    public ResponseEntity<ApiResponse<QuestionDetailResponse>> getQuestionDetail(
            @PathVariable String formId,
            @PathVariable String questionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        GetQuestionDetailRequest getQuestionDetailRequest = GetQuestionDetailRequest.builder()
                .formId(formId)
                .questionId(questionId)
                .userId(jwtClaim.getUserId())
                .page(page)
                .size(size)
                .build();
        
        QuestionDetailResponse response = submissionService.getQuestionDetail(getQuestionDetailRequest);
        return ResponseEntity.ok(RootResponse.success(response, "Question detail retrieved successfully", request.getRequestURI()));
    }
}
