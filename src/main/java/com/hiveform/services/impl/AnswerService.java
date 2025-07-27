package com.hiveform.services.impl;

import com.hiveform.entities.Answer;
import com.hiveform.repository.AnswerRepository;
import com.hiveform.services.IAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnswerService implements IAnswerService {
    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public Answer createAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public Answer updateAnswer(UUID id, Answer answer) {
        if (answerRepository.existsById(id)) {
            answer.setId(id);
            return answerRepository.save(answer);
        }
        return null;
    }

    @Override
    public void deleteAnswer(UUID id) {
        answerRepository.deleteById(id);
    }
}
