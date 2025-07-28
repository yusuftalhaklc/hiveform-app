package com.hiveform.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.hiveform.services.IQuestionService;
import com.hiveform.dto.question.DtoQuestionUpdate;
import com.hiveform.dto.question.DtoQuestionDelete;
import com.hiveform.dto.question.DtoQuestionDetail;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;
import com.hiveform.security.JwtClaim;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private IQuestionService questionService;

    @PutMapping("/{questionId}")
    public ResponseEntity<ApiResponse<DtoQuestionDetail>> updateQuestion(
            @Valid @RequestBody DtoQuestionUpdate updateQuestionRequestDto,
            @PathVariable String questionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request) {
        
        updateQuestionRequestDto.setQuestionId(questionId);
        updateQuestionRequestDto.setUserId(jwtClaim.getUserId());
        
        DtoQuestionDetail response = questionService.updateQuestion(updateQuestionRequestDto, jwtClaim.getUserId());
        return ResponseEntity.ok(RootResponse.success(response, "Question updated successfully", request.getRequestURI()));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable String questionId,
            @AuthenticationPrincipal JwtClaim jwtClaim,
            HttpServletRequest request) {
        
        DtoQuestionDelete deleteRequest = new DtoQuestionDelete();
        deleteRequest.setQuestionId(questionId);
        deleteRequest.setUserId(jwtClaim.getUserId());
        
        questionService.deleteQuestion(deleteRequest, jwtClaim.getUserId());
        return ResponseEntity.ok(RootResponse.success(null, "Question deleted successfully", request.getRequestURI()));
    }
}
