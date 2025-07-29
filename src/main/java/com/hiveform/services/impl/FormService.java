package com.hiveform.services.impl;
import org.springframework.beans.BeanUtils;

import com.hiveform.dto.form.FormDeleteRequest;
import com.hiveform.dto.form.FormDetailResponse;
import com.hiveform.dto.form.FormRequest;
import com.hiveform.dto.form.FormResponse;
import com.hiveform.dto.form.FormUpdateRequest;
import com.hiveform.dto.form.FormListResponse;
import com.hiveform.dto.form.FormListPageResponse;
import com.hiveform.dto.form.GetUserFormsRequest;
import com.hiveform.dto.question.QuestionDetailResponse;
import com.hiveform.dto.user.UserInfoResponse;
import com.hiveform.entities.Form;
import com.hiveform.repository.FormRepository;
import com.hiveform.services.IFormService;
import com.hiveform.entities.User;
import com.hiveform.exception.ResourceNotFoundException;
import com.hiveform.exception.UnauthorizedException;
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
    public FormResponse createForm(FormRequest createFormRequestDto) {
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

        FormResponse dtoForm = FormResponse.builder()
            .id(savedForm.getId().toString())
            .shortLink(savedForm.getShortLink())
            .build();
        return dtoForm;
    }
    
    @Override
    public FormResponse updateForm(FormUpdateRequest updateFormRequestDto, String userId) {
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
        FormResponse response = FormResponse.builder()
            .id(updatedForm.getId().toString())
            .shortLink(updatedForm.getShortLink())
            .build();
        return response;
    }

    @Override
    public FormDetailResponse getFormByShortLink(String shortLink) {
        Optional<Form> optionalForm = formRepository.findByShortLink(shortLink);
        if (optionalForm.isEmpty()) {
            throw new ResourceNotFoundException("Form not found with short link: " + shortLink);
        }

        Form form = optionalForm.get();

        FormDetailResponse dtoFormDetail = FormDetailResponse.builder()
            .id(form.getId().toString())
            .shortLink(form.getShortLink())
            .title(form.getTitle())
            .description(form.getDescription())
            .bannerImageUrl(form.getBannerImageUrl())
            .isActive(form.getIsActive())
            .isPublic(form.getIsPublic())
            .expiresAt(form.getExpiresAt())
            .createdAt(form.getCreatedAt())
            .updatedAt(form.getUpdatedAt())
            .questions(new ArrayList<>())
            .build();

        if (form.getQuestions() != null) {
            for (Question question : form.getQuestions()) {
                            QuestionDetailResponse dtoQuestion = QuestionDetailResponse.builder()
                .id(question.getId().toString())
                .formId(form.getId().toString())
                .title(question.getTitle())
                .description(question.getDescription())
                .questionIndex(question.getQuestionIndex())
                .imageUrl(question.getImageUrl())
                .type(question.getType().name())
                .isRequired(question.getIsRequired())
                .options(question.getOptions())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
            dtoFormDetail.getQuestions().add(dtoQuestion);
            }
        }

        if (form.getUser() != null) {
            UserInfoResponse userInfo = UserInfoResponse.builder()
                .id(form.getUser().getId().toString())
                .fullName(form.getUser().getFullName())
                .build();
            dtoFormDetail.setCreatedBy(userInfo);
        }


        return dtoFormDetail;
    }

    @Override
    @Transactional
    public void deleteFormById(FormDeleteRequest deleteRequest) {
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
    public FormListPageResponse getUserForms(String userId, GetUserFormsRequest request) {
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

        List<FormListResponse> formList = new ArrayList<>();
        for (Form form : formPage.getContent()) {
            // Get submission count from SubmissionRepository
            Long submissionCount = submissionRepository.countByFormId(form.getId());
            
            FormListResponse dtoForm = FormListResponse.builder()
                .id(form.getId().toString())
                .shortLink(form.getShortLink())
                .title(form.getTitle())
                .expiresAt(form.getExpiresAt())
                .createdAt(form.getCreatedAt())
                .updatedAt(form.getUpdatedAt())
                .isActive(form.getIsActive())
                .isPublic(form.getIsPublic())
                .submissionCount(submissionCount)
                .build();
            
            formList.add(dtoForm);
        }

        FormListPageResponse response = FormListPageResponse.builder()
            .forms(formList)
            .currentPage(page)
            .pageSize(size)
            .totalElements(formPage.getTotalElements())
            .totalPages(formPage.getTotalPages())
            .hasNext(formPage.hasNext())
            .hasPrevious(formPage.hasPrevious())
            .build();

        return response;
    }


}
