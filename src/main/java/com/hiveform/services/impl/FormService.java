package com.hiveform.services.impl;

import com.hiveform.entities.Form;
import com.hiveform.repository.FormRepository;
import com.hiveform.services.IFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FormService implements IFormService {
    @Autowired
    private FormRepository formRepository;

    @Override
    public Form createForm(Form form) {
        return formRepository.save(form);
    }

    @Override
    public Form updateForm(UUID id, Form form) {
        if (formRepository.existsById(id)) {
            form.setId(id);
            return formRepository.save(form);
        }
        return null;
    }

    @Override
    public void deleteForm(UUID id) {
        formRepository.deleteById(id);
    }
}
