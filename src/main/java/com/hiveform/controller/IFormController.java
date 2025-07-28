package com.hiveform.controller;

import org.springframework.http.ResponseEntity;

import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.form.DtoFormDetail;
import com.hiveform.dto.form.DtoFormIU;
import com.hiveform.dto.form.DtoFormUpdate;

import jakarta.servlet.http.HttpServletRequest;

public interface IFormController {
    public ResponseEntity<ApiResponse<DtoFormIUResponse>> createForm(DtoFormIU createFormRequestDto, HttpServletRequest request);
    public ResponseEntity<ApiResponse<DtoFormDetail>> getFormByShortLink(String shortLink, HttpServletRequest request);
    public ResponseEntity<ApiResponse<DtoFormIUResponse>> updateForm(DtoFormUpdate updateFormRequestDto, String formId, HttpServletRequest request);
    public ResponseEntity<?> deleteFormById(String formId, HttpServletRequest request);
}
