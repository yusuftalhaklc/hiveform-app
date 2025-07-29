package com.hiveform.services;

import com.hiveform.dto.form.DtoFormDelete;
import com.hiveform.dto.form.DtoFormIU;
import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.form.DtoFormDetail;
import com.hiveform.dto.form.DtoFormUpdate;
import com.hiveform.dto.form.DtoFormListResponse;
import com.hiveform.dto.form.DtoGetUserFormsRequest;

public interface IFormService {
    DtoFormIUResponse createForm(DtoFormIU createFormRequestDto);
    DtoFormIUResponse updateForm(DtoFormUpdate updateFormRequestDto, String userId);
    DtoFormDetail getFormByShortLink(String shortLink);
    void deleteFormById(DtoFormDelete deleteRequest);
    DtoFormListResponse getUserForms(String userId, DtoGetUserFormsRequest request);
}
