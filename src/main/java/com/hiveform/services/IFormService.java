package com.hiveform.services;

import com.hiveform.dto.form.DtoFormDelete;
import com.hiveform.dto.form.DtoFormIU;
import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.form.DtoFormDetail;

public interface IFormService {
    DtoFormIUResponse createForm(DtoFormIU createFormRequestDto);
    DtoFormIUResponse updateForm(DtoFormIU updateFormRequestDto);
    DtoFormDetail getFormByShortLink(String shortLink);
    void deleteFormById(DtoFormDelete deleteRequest);
}
