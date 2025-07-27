package com.hiveform.services.impl;

import com.hiveform.entities.Submission;
import com.hiveform.repository.SubmissionRepository;
import com.hiveform.services.ISubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubmissionService implements ISubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public Submission updateSubmission(UUID id, Submission submission) {
        if (submissionRepository.existsById(id)) {
            submission.setId(id);
            return submissionRepository.save(submission);
        }
        return null;
    }

    @Override
    public void deleteSubmission(UUID id) {
        submissionRepository.deleteById(id);
    }
}
