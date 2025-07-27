package com.hiveform.services;

import com.hiveform.entities.Question;
import java.util.UUID;

public interface IQuestionService {
    Question createQuestion(Question question);
    Question updateQuestion(UUID id, Question question);
    void deleteQuestion(UUID id);
}
