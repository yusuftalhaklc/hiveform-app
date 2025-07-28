package com.hiveform.services.impl;
import org.springframework.beans.BeanUtils;

import com.hiveform.dto.form.DtoFormDelete;
import com.hiveform.dto.form.DtoFormDetail;
import com.hiveform.dto.form.DtoFormIU;
import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.question.DtoQuestionDetail;
import com.hiveform.dto.user.DtoUserInfo;
import com.hiveform.entities.Form;
import com.hiveform.repository.FormRepository;
import com.hiveform.services.IFormService;
import com.hiveform.entities.User;
import com.hiveform.handler.ResourceNotFoundException;
import com.hiveform.handler.UnauthorizedException;
import com.hiveform.entities.Question;
import com.hiveform.repository.UserRepository;
import com.hiveform.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FormService implements IFormService {
    
    @Autowired
    private FormRepository formRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public DtoFormIUResponse createForm(DtoFormIU createFormRequestDto) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(createFormRequestDto.getUserId()));
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User not found with ID: " + createFormRequestDto.getUserId());    
        }

        User user = optionalUser.get();

        Form form = new Form();
        BeanUtils.copyProperties(createFormRequestDto, form);
        form.setUser(user);
        form.setIsActive(createFormRequestDto.getIsActive() != null ? createFormRequestDto.getIsActive() : true);
        form.setIsPublic(createFormRequestDto.getIsPublic() != null ? createFormRequestDto.getIsPublic() : false);

        String shortLink;
        do {
            shortLink = generateShortLink(8);
        } while (formRepository.existsByShortLink(shortLink));

        form.setShortLink(shortLink);
        Form savedForm = formRepository.save(form);

        List<Question> questionEntities = new ArrayList<>();
        if (createFormRequestDto.getQuestions() != null) {
            for (var dtoQuestion : createFormRequestDto.getQuestions()) {
                Question question = new Question();
                BeanUtils.copyProperties(dtoQuestion, question);
                question.setType(com.hiveform.enums.QuestionType.valueOf(dtoQuestion.getType()));
                question.setIsRequired(dtoQuestion.getIsRequired() != null ? dtoQuestion.getIsRequired() : false);
                question.setForm(savedForm);
                questionEntities.add(question);
            }
            questionRepository.saveAll(questionEntities);
        }

        DtoFormIUResponse dtoForm = new DtoFormIUResponse();
        dtoForm.setId(savedForm.getId().toString());
        dtoForm.setShortLink(savedForm.getShortLink());
        return dtoForm;
    }
    
    @Override
    public DtoFormIUResponse updateForm(DtoFormIU updateFormRequestDto) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(updateFormRequestDto.getUserId()));
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User not found with ID: " + updateFormRequestDto.getUserId());    
        }
        Optional<Form> optionalForm = formRepository.findById(UUID.fromString(updateFormRequestDto.getFormId()));
        if (optionalForm.isEmpty()) {
            throw new ResourceNotFoundException("Form not found with ID: " + updateFormRequestDto.getFormId());
        }

        User user = optionalUser.get();
        Form form = optionalForm.get();

        if (!form.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("User does not have permission to update this form");
        }

        form.setTitle(updateFormRequestDto.getTitle());
        form.setDescription(updateFormRequestDto.getDescription());
        form.setBannerImageUrl(updateFormRequestDto.getBannerImageUrl());
        form.setIsActive(updateFormRequestDto.getIsActive());
        form.setIsPublic(updateFormRequestDto.getIsPublic());
        form.setExpiresAt(updateFormRequestDto.getExpiresAt());

        Form updatedForm = formRepository.save(form);

        DtoFormIUResponse response = new DtoFormIUResponse();
        response.setId(updatedForm.getId().toString());
        response.setShortLink(updatedForm.getShortLink());
        return response;
    }

    @Override
    public DtoFormDetail getFormByShortLink(String shortLink) {
        Optional<Form> optionalForm = formRepository.findByShortLink(shortLink);
        if (optionalForm.isEmpty()) {
            throw new ResourceNotFoundException("Form not found with short link: " + shortLink);
        }

        Form form = optionalForm.get();

        DtoFormDetail dtoFormDetail = new DtoFormDetail();
        BeanUtils.copyProperties(form, dtoFormDetail);

        dtoFormDetail.setId(form.getId().toString());
        dtoFormDetail.setQuestions(new ArrayList<>());

        if (form.getQuestions() != null) {
            for (Question question : form.getQuestions()) {
                DtoQuestionDetail dtoQuestion = new DtoQuestionDetail();
                BeanUtils.copyProperties(question, dtoQuestion);
                dtoQuestion.setId(question.getId().toString());
                dtoQuestion.setFormId(form.getId().toString());
                dtoQuestion.setType(question.getType().name());
                dtoFormDetail.getQuestions().add(dtoQuestion);
            }
        }

        if (form.getUser() != null) {
            dtoFormDetail.setCreatedBy(new DtoUserInfo());
            BeanUtils.copyProperties(form.getUser(), dtoFormDetail.getCreatedBy());
            dtoFormDetail.getCreatedBy().setId(form.getUser().getId().toString());
        }


        return dtoFormDetail;
    }

    @Override
    public void deleteFormById(DtoFormDelete deleteRequest) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(deleteRequest.getUserId()));
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User not found with ID: " + deleteRequest.getUserId());    
        }
        Optional<Form> optionalForm = formRepository.findById(UUID.fromString(deleteRequest.getFormId()));
        if (optionalForm.isEmpty()) {
            throw new ResourceNotFoundException("Form not found with ID: " + deleteRequest.getFormId());
        }

        User user = optionalUser.get();
        Form form = optionalForm.get();

        if (!form.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("User does not have permission to delete this form");
        }

        formRepository.delete(form);
    }

    private String generateShortLink(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }
}
