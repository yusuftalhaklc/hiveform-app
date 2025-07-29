package com.hiveform.services;

import com.hiveform.entities.Question;
import com.hiveform.dto.question.QuestionUpdateRequest;
import com.hiveform.dto.question.QuestionDeleteRequest;
import com.hiveform.dto.question.QuestionDetailResponse;

public interface IQuestionService {
    Question createQuestion(Question question);
    QuestionDetailResponse updateQuestion(QuestionUpdateRequest updateQuestionRequestDto, String userId);
    void deleteQuestion(QuestionDeleteRequest deleteRequest, String userId);
}
