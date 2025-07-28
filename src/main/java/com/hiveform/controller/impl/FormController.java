package com.hiveform.controller.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiveform.controller.IFormController;
import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.form.DtoFormDelete;
import com.hiveform.dto.form.DtoFormDetail;
import com.hiveform.dto.form.DtoFormIU;
import com.hiveform.services.IFormService;

import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/form")
public class FormController implements IFormController {

    @Autowired
    private IFormService formService;

    @PostMapping("")
    public DtoFormIUResponse createForm(@RequestBody DtoFormIU createFormRequestDto, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        createFormRequestDto.setUserId(userId);
        return formService.createForm(createFormRequestDto);
    }

    @GetMapping("/{shortLink}")
    public DtoFormDetail getFormByShortLink(@PathVariable String shortLink) {
        return formService.getFormByShortLink(shortLink);
    }

    @DeleteMapping("/{formId}")
    @Override
    public ResponseEntity<?> deleteFormById(@PathVariable String formId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        DtoFormDelete deleteRequest = new DtoFormDelete();
        deleteRequest.setFormId(formId);
        deleteRequest.setUserId(userId);
        formService.deleteFormById(deleteRequest);
        throw new UnsupportedOperationException("Unimplemented method 'deleteFormById'");
    }

}
