package com.hiveform.controller;

import org.springframework.http.ResponseEntity;

import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.form.DtoFormDetail;
import com.hiveform.dto.form.DtoFormIU;

import jakarta.servlet.http.HttpServletRequest;

public interface IFormController {
    public DtoFormIUResponse createForm(DtoFormIU createFormRequestDto, HttpServletRequest request);
    public DtoFormDetail getFormByShortLink(String shortLink);
    public ResponseEntity<?> deleteFormById(String formId, HttpServletRequest request);
}
