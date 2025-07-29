package com.hiveform.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.hiveform.services.IQuestionService;
import com.hiveform.dto.question.QuestionUpdateRequest;
import com.hiveform.dto.question.QuestionDeleteRequest;
import com.hiveform.dto.question.QuestionDetailResponse;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;
import com.hiveform.security.JwtClaim;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/question")
@Tag(name = "Question", description = "Question API")
public class QuestionController {

    @Autowired
    private IQuestionService questionService;

    @PutMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionDetailResponse>> updateQuestion(
            @Valid @RequestBody QuestionUpdateRequest updateQuestionRequestDto,
            @PathVariable String questionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request) {
        
        updateQuestionRequestDto.setQuestionId(questionId);
        updateQuestionRequestDto.setUserId(jwtClaim.getUserId());
        
        QuestionDetailResponse response = questionService.updateQuestion(updateQuestionRequestDto, jwtClaim.getUserId());
        return ResponseEntity.ok(RootResponse.success(response, "Question updated successfully", request.getRequestURI()));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable String questionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request) {
        
        QuestionDeleteRequest deleteRequest = QuestionDeleteRequest.builder()
            .questionId(questionId)
            .userId(jwtClaim.getUserId())
            .build();
        
        questionService.deleteQuestion(deleteRequest, jwtClaim.getUserId());
        return ResponseEntity.ok(RootResponse.success(null, "Question deleted successfully", request.getRequestURI()));
    }
}
