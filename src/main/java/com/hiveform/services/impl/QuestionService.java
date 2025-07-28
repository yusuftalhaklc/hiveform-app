package com.hiveform.services.impl;

import com.hiveform.entities.Question;
import com.hiveform.entities.Form;
import com.hiveform.repository.QuestionRepository;
import com.hiveform.repository.FormRepository;
import com.hiveform.services.IQuestionService;
import com.hiveform.dto.question.DtoQuestionUpdate;
import com.hiveform.dto.question.DtoQuestionDelete;
import com.hiveform.dto.question.DtoQuestionDetail;
import com.hiveform.handler.ResourceNotFoundException;
import com.hiveform.handler.ForbiddenException;
import com.hiveform.enums.QuestionType;

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
    public DtoQuestionDetail updateQuestion(DtoQuestionUpdate updateQuestionRequestDto, String userId) {
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
        
        DtoQuestionDetail dtoQuestionDetail = new DtoQuestionDetail();
        dtoQuestionDetail.setId(updatedQuestion.getId().toString());
        dtoQuestionDetail.setFormId(updatedQuestion.getForm().getId().toString());
        dtoQuestionDetail.setTitle(updatedQuestion.getTitle());
        dtoQuestionDetail.setDescription(updatedQuestion.getDescription());
        dtoQuestionDetail.setQuestionIndex(updatedQuestion.getQuestionIndex());
        dtoQuestionDetail.setImageUrl(updatedQuestion.getImageUrl());
        dtoQuestionDetail.setType(updatedQuestion.getType().name());
        dtoQuestionDetail.setIsRequired(updatedQuestion.getIsRequired());
        dtoQuestionDetail.setOptions(updatedQuestion.getOptions());
        dtoQuestionDetail.setCreatedAt(updatedQuestion.getCreatedAt());
        dtoQuestionDetail.setUpdatedAt(updatedQuestion.getUpdatedAt());
        
        return dtoQuestionDetail;
    }

    @Override
    public void deleteQuestion(DtoQuestionDelete deleteRequest, String userId) {
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
