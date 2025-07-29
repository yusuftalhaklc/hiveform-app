package com.hiveform.controller;

import org.springframework.http.ResponseEntity;

import com.hiveform.dto.form.FormResponse;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.form.FormDetailResponse;
import com.hiveform.dto.form.FormRequest;
import com.hiveform.dto.form.FormUpdateRequest;
import com.hiveform.dto.form.FormListPageResponse;
import com.hiveform.security.JwtClaim;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface IFormController {
    public ResponseEntity<ApiResponse<FormResponse>> createForm(FormRequest createFormRequestDto, @AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request);
    public ResponseEntity<ApiResponse<FormDetailResponse>> getFormByShortLink(String shortLink, HttpServletRequest request);
    public ResponseEntity<ApiResponse<FormResponse>> updateForm(FormUpdateRequest updateFormRequestDto,  String formId, @AuthenticationPrincipal JwtClaim jwtClaim,HttpServletRequest request);
    public ResponseEntity<ApiResponse<Void>> deleteFormById(String formId,  @AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request);
    public ResponseEntity<ApiResponse<FormListPageResponse>> getUserForms(@AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request, int page, int size);
}
