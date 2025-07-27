package com.hiveform.services.impl;

import com.hiveform.entities.Question;
import com.hiveform.repository.QuestionRepository;
import com.hiveform.services.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuestionService implements IQuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(UUID id, Question question) {
        if (questionRepository.existsById(id)) {
            question.setId(id);
            return questionRepository.save(question);
        }
        return null;
    }

    @Override
    public void deleteQuestion(UUID id) {
        questionRepository.deleteById(id);
    }
}
