package com.hiveform.services.impl;

import com.hiveform.entities.Submission;
import com.hiveform.entities.Form;
import com.hiveform.entities.Question;
import com.hiveform.entities.Answer;
import com.hiveform.repository.SubmissionRepository;
import com.hiveform.repository.FormRepository;
import com.hiveform.repository.QuestionRepository;
import com.hiveform.repository.AnswerRepository;
import com.hiveform.services.ISubmissionService;
import com.hiveform.dto.submission.SubmissionRequest;
import com.hiveform.dto.submission.SubmissionResponse;
import com.hiveform.dto.submission.SubmissionListResponse;
import com.hiveform.exception.ResourceNotFoundException;
import com.hiveform.exception.ForbiddenException;
import com.hiveform.entities.User;
import com.hiveform.repository.UserRepository;
import com.hiveform.dto.submission.GetSubmissionsRequest;
import com.hiveform.dto.submission.DeleteSubmissionRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubmissionService implements ISubmissionService {
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private FormRepository formRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public SubmissionResponse createSubmission(SubmissionRequest submissionRequest, String userId) {
        Optional<Form> formOptional = formRepository.findById(UUID.fromString(submissionRequest.getFormId()));

        if (formOptional.isEmpty()) {
            throw new ResourceNotFoundException("Form not found");
        }
        
        Form form = formOptional.get();
    
        
        Submission submission = new Submission();
        submission.setForm(form);
        submission.setSubmittedAt(System.currentTimeMillis() / 1000);
        
        if (userId != null) {
            submission.setSubmissionBy(UUID.fromString(userId));
        }
        
        Submission savedSubmission = submissionRepository.save(submission);

        List<Answer> answers = submissionRequest.getAnswers().stream()
                .map(answerRequest -> {
                    Optional<Question> questionOptional = questionRepository.findById(UUID.fromString(answerRequest.getQuestionId()));

                    if (questionOptional.isEmpty()) {
                        throw new ResourceNotFoundException("Question not found");
                    }

                    Question question = questionOptional.get();
                    
                    Answer answer = new Answer();
                    answer.setSubmission(savedSubmission);
                    answer.setQuestion(question);
                    answer.setAnswerText(answerRequest.getAnswerText());
                    answer.setSelectedOption(answerRequest.getSelectedOption());
                    answer.setSelectedOptions(answerRequest.getSelectedOptions());
                    answer.setFileUrl(answerRequest.getFileUrl());
                    
                    if (answerRequest.getSelectedDate() != null) {
                        answer.setSelectedDate(LocalDate.parse(answerRequest.getSelectedDate()));
                    }
                    if (answerRequest.getSelectedTime() != null) {
                        answer.setSelectedTime(LocalTime.parse(answerRequest.getSelectedTime()));
                    }
                    
                    answer.setSelectedRating(answerRequest.getSelectedRating());
                    
                    return answer;
                })
                .collect(Collectors.toList());
        
        answerRepository.saveAll(answers);
        
        return buildSubmissionResponse(savedSubmission, answers);
    }

    @Override
    public SubmissionResponse getSubmissionById(String submissionId) {
        Submission submission = submissionRepository.findById(UUID.fromString(submissionId))
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        
        List<Answer> answers = answerRepository.findBySubmissionId(submission.getId());
        return buildSubmissionResponse(submission, answers);
    }

    @Override
    public SubmissionListResponse getSubmissionsByFormId(GetSubmissionsRequest request) {
        Optional<Form> formOptional = formRepository.findById(UUID.fromString(request.getFormId()));

        if (formOptional.isEmpty()) {
            throw new ResourceNotFoundException("Form not found");
        }
        
        Form form = formOptional.get();
        
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<Submission> submissionPage = submissionRepository.findByFormId(form.getId(), pageable);
        
        return buildSubmissionListResponse(submissionPage);
    }

    @Override
    public SubmissionListResponse getUserFormSubmissions(GetSubmissionsRequest request) {
        Optional<Form> formOptional = formRepository.findById(UUID.fromString(request.getFormId()));

        if (formOptional.isEmpty()) {
            throw new ResourceNotFoundException("Form not found");
        }
        
        Form form = formOptional.get();
        
        if (!form.getUser().getId().toString().equals(request.getUserId())) {
            throw new ForbiddenException("You don't have permission to view submissions for this form");
        }
        
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<Submission> submissionPage = submissionRepository.findByFormId(form.getId(), pageable);
        
        return buildSubmissionListResponse(submissionPage);
    }

    @Override
    @Transactional
    public void deleteSubmission(DeleteSubmissionRequest request) {
        Optional<Submission> submissionOptional = submissionRepository.findById(UUID.fromString(request.getSubmissionId()));

        if (submissionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Submission not found");
        }

        Submission submission = submissionOptional.get();

        if (!submission.getForm().getUser().getId().toString().equals(request.getUserId())) {
             throw new ForbiddenException("You don't have permission to delete this submission");
        }
        
        answerRepository.deleteBySubmissionId(submission.getId());
        
        submissionRepository.delete(submission);
    }

    private SubmissionResponse buildSubmissionResponse(Submission submission, List<Answer> answers) {
        List<SubmissionResponse.AnswerResponse> answerResponses = answers.stream()
                .map(answer -> SubmissionResponse.AnswerResponse.builder()
                        .id(answer.getId().toString())
                        .questionId(answer.getQuestion().getId().toString())
                        .questionTitle(answer.getQuestion().getTitle())
                        .questionType(answer.getQuestion().getType().toString())
                        .answerText(answer.getAnswerText())
                        .selectedOption(answer.getSelectedOption())
                        .selectedOptions(answer.getSelectedOptions())
                        .fileUrl(answer.getFileUrl())
                        .selectedDate(answer.getSelectedDate() != null ? answer.getSelectedDate().toString() : null)
                        .selectedTime(answer.getSelectedTime() != null ? answer.getSelectedTime().toString() : null)
                        .selectedRating(answer.getSelectedRating())
                        .build())
                .collect(Collectors.toList());

        SubmissionResponse.SubmissionByUser submissionByUser = null;
        if (submission.getSubmissionBy() != null) {
            try {
                User user = userRepository.findById(submission.getSubmissionBy())
                        .orElse(null);
                if (user != null) {
                    submissionByUser = SubmissionResponse.SubmissionByUser.builder()
                            .id(user.getId().toString())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .build();
                }
            } catch (Exception e) {
            
            }
        }

        return SubmissionResponse.builder()
                .id(submission.getId().toString())
                .formId(submission.getForm().getShortLink())
                .formTitle(submission.getForm().getTitle())
                .submittedAt(submission.getSubmittedAt())
                .submissionBy(submission.getSubmissionBy() != null ? submission.getSubmissionBy().toString() : null)
                .submissionByUser(submissionByUser)
                .answers(answerResponses)
                .build();
    }

    private SubmissionListResponse buildSubmissionListResponse(Page<Submission> submissionPage) {
        List<SubmissionListResponse.SubmissionSummary> summaries = submissionPage.getContent().stream()
                .map(submission -> {
                    List<Answer> answers = answerRepository.findBySubmissionId(submission.getId());
                    
                    SubmissionListResponse.SubmissionByUser submissionByUser = null;
                    if (submission.getSubmissionBy() != null) {
                        try {
                            User user = userRepository.findById(submission.getSubmissionBy())
                                    .orElse(null);
                            if (user != null) {
                                submissionByUser = SubmissionListResponse.SubmissionByUser.builder()
                                        .id(user.getId().toString())
                                        .fullName(user.getFullName())
                                        .email(user.getEmail())
                                        .build();
                            }
                        } catch (Exception e) {
                            
                        }
                    }
                    
                    return SubmissionListResponse.SubmissionSummary.builder()
                            .id(submission.getId().toString())
                            .formId(submission.getForm().getShortLink())
                            .formTitle(submission.getForm().getTitle())
                            .submittedAt(submission.getSubmittedAt())
                            .submissionBy(submission.getSubmissionBy() != null ? submission.getSubmissionBy().toString() : null)
                            .submissionByUser(submissionByUser)
                            .answerCount(answers.size())
                            .build();
                })
                .collect(Collectors.toList());

        return SubmissionListResponse.builder()
                .submissions(summaries)
                .currentPage(submissionPage.getNumber() + 1)
                .totalPages(submissionPage.getTotalPages())
                .totalElements(submissionPage.getTotalElements())
                .pageSize(submissionPage.getSize())
                .build();
    }
}
