package com.hiveform.services;

import com.hiveform.dto.form.FormDeleteRequest;
import com.hiveform.dto.form.FormRequest;
import com.hiveform.dto.form.FormResponse;
import com.hiveform.dto.form.FormDetailResponse;
import com.hiveform.dto.form.FormUpdateRequest;
import com.hiveform.dto.form.FormListPageResponse;
import com.hiveform.dto.form.GetUserFormsRequest;

public interface IFormService {
    FormResponse createForm(FormRequest createFormRequestDto);
    FormResponse updateForm(FormUpdateRequest updateFormRequestDto, String userId);
    FormDetailResponse getFormByShortLink(String shortLink);
    void deleteFormById(FormDeleteRequest deleteRequest);
    FormListPageResponse getUserForms(String userId, GetUserFormsRequest request);
}
