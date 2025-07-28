package com.hiveform.controller.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiveform.controller.IFormController;
import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;
import com.hiveform.dto.form.DtoFormDelete;
import com.hiveform.dto.form.DtoFormDetail;
import com.hiveform.dto.form.DtoFormIU;
import com.hiveform.dto.form.DtoFormUpdate;
import com.hiveform.services.IFormService;

import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/form")
public class FormController implements IFormController {

    @Autowired
    private IFormService formService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<DtoFormIUResponse>> createForm(@Valid @RequestBody DtoFormIU createFormRequestDto, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        createFormRequestDto.setUserId(userId);
        return ResponseEntity.ok(RootResponse.success(formService.createForm(createFormRequestDto), "Form created successfully",request.getRequestURI()));
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<ApiResponse<DtoFormDetail>> getFormByShortLink(@PathVariable String shortLink, HttpServletRequest request) {
        return ResponseEntity.ok(RootResponse.success(formService.getFormByShortLink(shortLink), "Form retrieved successfully",request.getRequestURI()));
    }

    @DeleteMapping("/{formId}")
    @Override
    public ResponseEntity<?> deleteFormById(@PathVariable String formId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        DtoFormDelete deleteRequest = new DtoFormDelete();
        deleteRequest.setFormId(formId);
        deleteRequest.setUserId(userId);
        formService.deleteFormById(deleteRequest);
        return ResponseEntity.ok(RootResponse.success(null, "Form deleted successfully",request.getRequestURI()));
    }

    @PutMapping("/{formId}")
    @Override
    public ResponseEntity<ApiResponse<DtoFormIUResponse>> updateForm(@Valid @RequestBody DtoFormUpdate updateFormRequestDto, @PathVariable String formId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        DtoFormIUResponse response = formService.updateForm(updateFormRequestDto, userId);
        return ResponseEntity.ok(RootResponse.success(response, "Form updated successfully", request.getRequestURI()));
    }

}
