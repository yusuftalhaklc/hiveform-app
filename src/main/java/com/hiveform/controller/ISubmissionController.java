package com.hiveform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.submission.SubmissionRequest;
import com.hiveform.dto.submission.SubmissionResponse;
import com.hiveform.dto.submission.SubmissionListResponse;
import com.hiveform.dto.submission.FormSummaryResponse;
import com.hiveform.dto.submission.QuestionDetailResponse;
import com.hiveform.security.JwtClaim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface ISubmissionController {

    @PostMapping("")
    ResponseEntity<ApiResponse<SubmissionResponse>> createSubmission(
            @Valid @RequestBody SubmissionRequest submissionRequest,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request);

    @GetMapping("/{submissionId}")
    ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(
            @PathVariable String submissionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request);

    @GetMapping("/form/{formId}")
    ResponseEntity<ApiResponse<SubmissionListResponse>> getSubmissionsByFormId(
            @PathVariable String formId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size);

    @DeleteMapping("/{submissionId}")
    ResponseEntity<ApiResponse<Void>> deleteSubmission(
            @PathVariable String submissionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request);
    
    // New endpoints for form owner views
    @GetMapping("/form/{formId}/summary")
    ResponseEntity<ApiResponse<FormSummaryResponse>> getFormSummary(
            @PathVariable String formId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size);
    
    @GetMapping("/form/{formId}/question/{questionId}")
    ResponseEntity<ApiResponse<QuestionDetailResponse>> getQuestionDetail(
            @PathVariable String formId,
            @PathVariable String questionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size);
}
