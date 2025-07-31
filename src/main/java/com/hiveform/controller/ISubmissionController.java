package com.hiveform.controller;

import org.springframework.http.ResponseEntity;

import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.submission.SubmissionResponse;
import com.hiveform.security.JwtClaim;

import jakarta.servlet.http.HttpServletRequest;

public interface ISubmissionController {

    ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(String submissionId,JwtClaim jwtClaim,HttpServletRequest request);
    ResponseEntity<ApiResponse<Void>> deleteSubmission(String submissionId,JwtClaim jwtClaim,HttpServletRequest request);
}
