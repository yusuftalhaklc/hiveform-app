package com.hiveform.services.impl;
import org.springframework.beans.BeanUtils;

import com.hiveform.dto.form.DtoFormDelete;
import com.hiveform.dto.form.DtoFormDetail;
import com.hiveform.dto.form.DtoFormIU;
import com.hiveform.dto.form.DtoFormIUResponse;
import com.hiveform.dto.form.DtoFormUpdate;
import com.hiveform.dto.form.DtoFormList;
import com.hiveform.dto.form.DtoFormListResponse;
import com.hiveform.dto.form.DtoGetUserFormsRequest;
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
import com.hiveform.repository.SubmissionRepository;
import com.hiveform.infrastructure.redis.FormRedisRepository;
import com.hiveform.utils.SecureTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

@Service
public class FormService implements IFormService {
    
    @Autowired
    private FormRepository formRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private FormRedisRepository formRedisRepository;

    @Autowired
    private SecureTokenGenerator tokenGenerator;

    @Override
    @Transactional
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
            shortLink = tokenGenerator.generateSecureToken(8);
        } while (formRepository.findByShortLink(shortLink).isPresent());

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
    public DtoFormIUResponse updateForm(DtoFormUpdate updateFormRequestDto, String userId) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User not found with ID: " + userId);
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
        form.setIsActive(updateFormRequestDto.getIsActive() != null ? updateFormRequestDto.getIsActive() : form.getIsActive());
        form.setIsPublic(updateFormRequestDto.getIsPublic() != null ? updateFormRequestDto.getIsPublic() : form.getIsPublic());

        if (updateFormRequestDto.getBannerImageUrl() != null) {
            form.setBannerImageUrl(updateFormRequestDto.getBannerImageUrl());
        }

        if (updateFormRequestDto.getExpiresAt() != null) {
            form.setExpiresAt(updateFormRequestDto.getExpiresAt());
        }

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
    @Transactional
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

        String shortLink = form.getShortLink();
        
        formRepository.delete(form);
        
        clearFormCacheAsync(shortLink);
    }

    @Async
    public void clearFormCacheAsync(String shortLink) {
        try {
            formRedisRepository.deleteFormByShortlink(shortLink);
        } catch (Exception e) {
            System.err.println("Failed to clear form cache for shortLink: " + shortLink + ", Error: " + e.getMessage());
        }
    }

    @Override
    public DtoFormListResponse getUserForms(String userId, DtoGetUserFormsRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        
        if (size > 20) {
            size = 20;
        }
        if (size < 1) {
            size = 10; 
        }
        if (page <= 0) {
            page = 1;
        }

        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User not found with ID: " + userId);
        }

        // Convert to 0-based page for Spring Data JPA
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Form> formPage = formRepository.findByUserIdOrderByCreatedAtDesc(UUID.fromString(userId), pageable);

        List<DtoFormList> formList = new ArrayList<>();
        for (Form form : formPage.getContent()) {
            DtoFormList dtoForm = new DtoFormList();
            dtoForm.setId(form.getId().toString());
            dtoForm.setShortLink(form.getShortLink());
            dtoForm.setTitle(form.getTitle());
            dtoForm.setExpiresAt(form.getExpiresAt());
            dtoForm.setCreatedAt(form.getCreatedAt());
            dtoForm.setUpdatedAt(form.getUpdatedAt());
            dtoForm.setIsActive(form.getIsActive());
            dtoForm.setIsPublic(form.getIsPublic());
            
            // Get submission count from SubmissionRepository
            Long submissionCount = submissionRepository.countByFormId(form.getId());
            dtoForm.setSubmissionCount(submissionCount);
            
            formList.add(dtoForm);
        }

        DtoFormListResponse response = new DtoFormListResponse();
        response.setForms(formList);
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setTotalElements(formPage.getTotalElements());
        response.setTotalPages(formPage.getTotalPages());
        response.setHasNext(formPage.hasNext());
        response.setHasPrevious(formPage.hasPrevious());

        return response;
    }


}
