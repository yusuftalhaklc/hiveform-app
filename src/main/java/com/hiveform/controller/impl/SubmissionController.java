package com.hiveform.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hiveform.controller.ISubmissionController;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;
import com.hiveform.dto.submission.SubmissionResponse;
import com.hiveform.services.ISubmissionService;
import com.hiveform.security.JwtClaim;
import com.hiveform.dto.submission.DeleteSubmissionRequest;
import com.hiveform.dto.submission.GetSubmissionByIdRequest;

import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/submission")
@Tag(name = "Submission", description = "Form Submission API")
public class SubmissionController implements ISubmissionController {

    @Autowired
    private ISubmissionService submissionService;

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
    
}
