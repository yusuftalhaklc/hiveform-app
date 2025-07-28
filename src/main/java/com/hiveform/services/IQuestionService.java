package com.hiveform.services;

import com.hiveform.entities.Question;
import com.hiveform.dto.question.DtoQuestionUpdate;
import com.hiveform.dto.question.DtoQuestionDelete;
import com.hiveform.dto.question.DtoQuestionDetail;

public interface IQuestionService {
    Question createQuestion(Question question);
    DtoQuestionDetail updateQuestion(DtoQuestionUpdate updateQuestionRequestDto, String userId);
    void deleteQuestion(DtoQuestionDelete deleteRequest, String userId);
}
