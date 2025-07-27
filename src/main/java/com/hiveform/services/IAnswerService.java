package com.hiveform.services;

import com.hiveform.entities.Answer;
import java.util.UUID;

public interface IAnswerService {
    Answer createAnswer(Answer answer);
    Answer updateAnswer(UUID id, Answer answer);
    void deleteAnswer(UUID id);
}
