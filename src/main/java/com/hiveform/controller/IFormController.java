package com.hiveform.controller;

import org.springframework.http.ResponseEntity;

import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.form.DtoFormDetail;
import com.hiveform.dto.form.DtoFormIU;
import com.hiveform.dto.form.DtoFormUpdate;
import com.hiveform.dto.form.DtoFormListResponse;
import com.hiveform.security.JwtClaim;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface IFormController {
    public ResponseEntity<ApiResponse<DtoFormIUResponse>> createForm(DtoFormIU createFormRequestDto, @AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request);
    public ResponseEntity<ApiResponse<DtoFormDetail>> getFormByShortLink(String shortLink, HttpServletRequest request);
    public ResponseEntity<ApiResponse<DtoFormIUResponse>> updateForm(DtoFormUpdate updateFormRequestDto,  String formId, @AuthenticationPrincipal JwtClaim jwtClaim,HttpServletRequest request);
    public ResponseEntity<ApiResponse<Void>> deleteFormById(String formId,  @AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request);
    public ResponseEntity<ApiResponse<DtoFormListResponse>> getUserForms(@AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request, int page, int size);
}
