package com.hiveform.services.impl;

import com.hiveform.entities.Question;
import com.hiveform.entities.Form;
import com.hiveform.repository.QuestionRepository;
import com.hiveform.repository.FormRepository;
import com.hiveform.services.IQuestionService;
import com.hiveform.dto.question.QuestionUpdateRequest;
import com.hiveform.dto.question.QuestionDeleteRequest;
import com.hiveform.dto.question.QuestionDetailResponse;
import com.hiveform.enums.QuestionType;
import com.hiveform.exception.ForbiddenException;
import com.hiveform.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Optional;

@Service
public class QuestionService implements IQuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private FormRepository formRepository;

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public QuestionDetailResponse updateQuestion(QuestionUpdateRequest updateQuestionRequestDto, String userId) {
        UUID questionId = UUID.fromString(updateQuestionRequestDto.getQuestionId());
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isEmpty()) {
            throw new ResourceNotFoundException("Question not found");
        }
        
        Question question = optionalQuestion.get();
        
        Optional<Form> optionalForm = formRepository.findById(question.getForm().getId());
        if (optionalForm.isEmpty()) {
            throw new ResourceNotFoundException("Form not found");
        }
        
        Form form = optionalForm.get();
        
        if (!form.getUser().getId().toString().equals(userId)) {
            throw new ForbiddenException("You can only update questions in your own forms");
        }
        
        question.setTitle(updateQuestionRequestDto.getTitle());
        question.setDescription(updateQuestionRequestDto.getDescription());
        question.setIsRequired(updateQuestionRequestDto.getIsRequired());
        question.setQuestionIndex(updateQuestionRequestDto.getQuestionIndex());
        question.setImageUrl(updateQuestionRequestDto.getImageUrl());
        question.setType(QuestionType.valueOf(updateQuestionRequestDto.getType()));
        question.setOptions(updateQuestionRequestDto.getOptions());
        
        Question updatedQuestion = questionRepository.save(question);
        
        QuestionDetailResponse dtoQuestionDetail = QuestionDetailResponse.builder()
            .id(updatedQuestion.getId().toString())
            .formId(updatedQuestion.getForm().getId().toString())
            .title(updatedQuestion.getTitle())
            .description(updatedQuestion.getDescription())
            .questionIndex(updatedQuestion.getQuestionIndex())
            .imageUrl(updatedQuestion.getImageUrl())
            .type(updatedQuestion.getType().name())
            .isRequired(updatedQuestion.getIsRequired())
            .options(updatedQuestion.getOptions())
            .createdAt(updatedQuestion.getCreatedAt())
            .updatedAt(updatedQuestion.getUpdatedAt())
            .build();
        
        return dtoQuestionDetail;
    }

    @Override
    public void deleteQuestion(QuestionDeleteRequest deleteRequest, String userId) {
        UUID questionId = UUID.fromString(deleteRequest.getQuestionId());
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isEmpty()) {
            throw new ResourceNotFoundException("Question not found");
        }
        
        Question question = optionalQuestion.get();
        
        Optional<Form> optionalForm = formRepository.findById(question.getForm().getId());
        if (optionalForm.isEmpty()) {
            throw new ResourceNotFoundException("Form not found");
        }
        
        Form form = optionalForm.get();
        
        if (!form.getUser().getId().toString().equals(userId)) {
            throw new ForbiddenException("You can only delete questions in your own forms");
        }
        
        questionRepository.deleteById(questionId);
    }
}
