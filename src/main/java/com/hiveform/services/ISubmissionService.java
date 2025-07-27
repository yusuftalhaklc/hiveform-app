package com.hiveform.services;

import com.hiveform.entities.Submission;
import java.util.UUID;

public interface ISubmissionService {
    Submission createSubmission(Submission submission);
    Submission updateSubmission(UUID id, Submission submission);
    void deleteSubmission(UUID id);
}
