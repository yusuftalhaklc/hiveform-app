package com.hiveform.services;

import com.hiveform.entities.Form;
import java.util.UUID;

public interface IFormService {
    Form createForm(Form form);
    Form updateForm(UUID id, Form form);
    void deleteForm(UUID id);
}
