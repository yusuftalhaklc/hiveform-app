package com.hiveform.controller;

import org.springframework.http.ResponseEntity;

import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.question.QuestionDetailResponse;
import com.hiveform.dto.question.QuestionUpdateRequest;
import com.hiveform.security.JwtClaim;

import jakarta.servlet.http.HttpServletRequest;

public interface IQuestionController {

    ResponseEntity<ApiResponse<QuestionDetailResponse>> updateQuestion(
        QuestionUpdateRequest updateQuestionRequestDto,
        String questionId,
        JwtClaim jwtClaim,
        HttpServletRequest request);

    ResponseEntity<ApiResponse<Void>> deleteQuestion(
        String questionId,
        JwtClaim jwtClaim,
        HttpServletRequest request);
}
