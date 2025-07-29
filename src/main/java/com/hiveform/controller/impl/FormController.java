package com.hiveform.controller.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiveform.controller.IFormController;
import com.hiveform.dto.form.FormResponse;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;
import com.hiveform.dto.form.FormDetailResponse;
import com.hiveform.dto.form.FormRequest;
import com.hiveform.dto.form.FormUpdateRequest;
import com.hiveform.dto.form.FormListPageResponse;
import com.hiveform.dto.form.GetUserFormsRequest;
import com.hiveform.dto.form.FormDeleteRequest;
import com.hiveform.services.IFormService;
import com.hiveform.security.JwtClaim;

import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/form")
@Tag(name = "Form", description = "Form API")
public class FormController implements IFormController {

    @Autowired
    private IFormService formService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<FormResponse>> createForm(@Valid @RequestBody FormRequest createFormRequestDto, @AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request) {
        createFormRequestDto.setUserId(jwtClaim.getUserId());
        return ResponseEntity.ok(RootResponse.success(formService.createForm(createFormRequestDto), "Form created successfully",request.getRequestURI()));
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<ApiResponse<FormDetailResponse>> getFormByShortLink(@PathVariable String shortLink, HttpServletRequest request) {
        return ResponseEntity.ok(RootResponse.success(formService.getFormByShortLink(shortLink), "Form retrieved successfully",request.getRequestURI()));
    }

    @PutMapping("/{formId}")
    @Override
    public ResponseEntity<ApiResponse<FormResponse>> updateForm(@Valid @RequestBody FormUpdateRequest updateFormRequestDto, @PathVariable String formId, @AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request) {
        updateFormRequestDto.setFormId(formId);
        updateFormRequestDto.setUserId(jwtClaim.getUserId());
        FormResponse response = formService.updateForm(updateFormRequestDto, jwtClaim.getUserId());
        return ResponseEntity.ok(RootResponse.success(response, "Form updated successfully", request.getRequestURI()));
    }

    @DeleteMapping("/{formId}")
    @Override
    public ResponseEntity<ApiResponse<Void>> deleteFormById(@PathVariable String formId, @AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request) {
        FormDeleteRequest deleteRequest = FormDeleteRequest.builder()
            .formId(formId)
            .userId(jwtClaim.getUserId())
            .build();

        formService.deleteFormById(deleteRequest);

        return ResponseEntity.ok(RootResponse.success(null, "Form deleted successfully", request.getRequestURI()));
    }

    @GetMapping("/user/forms")
    @Override
    public ResponseEntity<ApiResponse<FormListPageResponse>> getUserForms(@AuthenticationPrincipal JwtClaim jwtClaim, HttpServletRequest request, 
                                                                        @RequestParam(defaultValue = "1") int page, 
                                                                        @RequestParam(defaultValue = "10") int size) {
        GetUserFormsRequest requestDto = new GetUserFormsRequest(page, size);
        FormListPageResponse response = formService.getUserForms(jwtClaim.getUserId(), requestDto);
        return ResponseEntity.ok(RootResponse.success(response, "User forms retrieved successfully", request.getRequestURI()));
    }

}
