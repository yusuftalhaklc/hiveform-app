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
import com.hiveform.dto.submission.GetSubmissionByIdRequest;
import com.hiveform.dto.submission.FormSummaryResponse;
import com.hiveform.dto.submission.GetFormSummaryRequest;
import com.hiveform.dto.submission.QuestionDetailResponse;
import com.hiveform.dto.submission.GetQuestionDetailRequest;

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
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
            submission.setSubmissionBy(userId);
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
    public SubmissionResponse getSubmissionById(GetSubmissionByIdRequest request) {
        Optional<Submission> submissionOptional = submissionRepository.findById(UUID.fromString(request.getSubmissionId()));

        if (submissionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Submission not found");
        }

        Submission submission = submissionOptional.get();

        String formOwnerId = submission.getForm().getUser().getId().toString();
        String submissionOwnerId = submission.getSubmissionBy();

        if (!formOwnerId.equals(request.getUserId()) && 
            (submissionOwnerId == null || !submissionOwnerId.equals(request.getUserId()))) {
            throw new ForbiddenException("You don't have permission to view this submission");
        }
        
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

    // New methods for form owner views
    @Override
    public FormSummaryResponse getFormSummary(GetFormSummaryRequest request) {
        Optional<Form> formOptional = formRepository.findById(UUID.fromString(request.getFormId()));

        if (formOptional.isEmpty()) {
            throw new ResourceNotFoundException("Form not found");
        }
        
        Form form = formOptional.get();
        
        // Check if user owns the form
        if (!form.getUser().getId().toString().equals(request.getUserId())) {
            throw new ForbiddenException("You don't have permission to view this form summary");
        }
        
        // Get total submissions count
        Long totalSubmissions = submissionRepository.countByFormId(form.getId());
        
        // Get all questions for the form
        List<Question> questions = questionRepository.findByFormIdOrderByQuestionIndex(form.getId());
        
        List<FormSummaryResponse.QuestionSummary> questionSummaries = questions.stream()
                .map(question -> buildQuestionSummary(question, form.getId(), request.getPage(), request.getSize()))
                .collect(Collectors.toList());
        
        return FormSummaryResponse.builder()
                .formId(form.getId().toString())
                .formTitle(form.getTitle())
                .totalSubmissions(totalSubmissions)
                .questionSummaries(questionSummaries)
                .build();
    }
    
    @Override
    public QuestionDetailResponse getQuestionDetail(GetQuestionDetailRequest request) {
        Optional<Form> formOptional = formRepository.findById(UUID.fromString(request.getFormId()));

        if (formOptional.isEmpty()) {
            throw new ResourceNotFoundException("Form not found");
        }
        
        Form form = formOptional.get();
        
        // Check if user owns the form
        if (!form.getUser().getId().toString().equals(request.getUserId())) {
            throw new ForbiddenException("You don't have permission to view this question detail");
        }
        
        Optional<Question> questionOptional = questionRepository.findById(UUID.fromString(request.getQuestionId()));
        
        if (questionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Question not found");
        }
        
        Question question = questionOptional.get();
        
        // Get all answers for this question with pagination
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<Answer> answerPage = answerRepository.findByQuestionIdOrderBySubmissionSubmittedAtDesc(
                question.getId(), pageable);
        
        List<QuestionDetailResponse.QuestionAnswer> answers = answerPage.getContent().stream()
                .map(answer -> {
                    // Get user details if submissionBy is not null
                    String submissionByUser = null;
                    if (answer.getSubmission().getSubmissionBy() != null) {
                        try {
                            User user = userRepository.findById(UUID.fromString(answer.getSubmission().getSubmissionBy()))
                                    .orElse(null);
                            if (user != null) {
                                submissionByUser = user.getFullName();
                            }
                        } catch (Exception e) {
                            // User not found or invalid UUID, leave as null
                        }
                    }
                    
                    return QuestionDetailResponse.QuestionAnswer.builder()
                            .submissionId(answer.getSubmission().getId().toString())
                            .submissionBy(submissionByUser)
                            .submittedAt(answer.getSubmission().getSubmittedAt())
                            .answerText(answer.getAnswerText())
                            .selectedOption(answer.getSelectedOption())
                            .selectedOptions(answer.getSelectedOptions())
                            .fileUrl(answer.getFileUrl())
                            .selectedDate(answer.getSelectedDate() != null ? answer.getSelectedDate().toString() : null)
                            .selectedTime(answer.getSelectedTime() != null ? answer.getSelectedTime().toString() : null)
                            .selectedRating(answer.getSelectedRating())
                            .build();
                })
                .collect(Collectors.toList());
        
        return QuestionDetailResponse.builder()
                .questionId(question.getId().toString())
                .questionTitle(question.getTitle())
                .questionType(question.getType().toString())
                .questionIndex(question.getQuestionIndex())
                .totalAnswers(answerPage.getTotalElements())
                .answers(answers)
                .build();
    }
    
    private FormSummaryResponse.QuestionSummary buildQuestionSummary(Question question, UUID formId, int page, int size) {
        List<Answer> allAnswers = answerRepository.findByQuestionId(question.getId());
        Long totalAnswers = (long) allAnswers.size();
        
        FormSummaryResponse.QuestionSummary.QuestionSummaryBuilder summaryBuilder = FormSummaryResponse.QuestionSummary.builder()
                .questionId(question.getId().toString())
                .questionTitle(question.getTitle())
                .questionType(question.getType().toString())
                .questionIndex(question.getQuestionIndex())
                .totalAnswers(totalAnswers);
        
        switch (question.getType()) {
            case SHORT_TEXT:
            case LONG_TEXT:
            case EMAIL:
            case URL:
            case NUMBER:
                summaryBuilder.textAnswerCount(totalAnswers);
                summaryBuilder.textAnswerSamples(buildTextAnswerSamples(allAnswers, page, size));
                break;
                
            case SINGLE_CHOICE:
            case DROPDOWN:
                summaryBuilder.choiceOptionStats(buildChoiceOptionStats(allAnswers));
                break;
                
            case MULTIPLE_CHOICE:
                summaryBuilder.choiceOptionStats(buildMultipleChoiceOptionStats(allAnswers));
                break;
                
            case RATING:
                summaryBuilder.ratingCount(totalAnswers);
                summaryBuilder.averageRating(calculateAverageRating(allAnswers));
                summaryBuilder.ratingDistribution(buildRatingDistribution(allAnswers));
                break;
                
            case DATE:
            case TIME:
                summaryBuilder.dateTimeAnswerCount(totalAnswers);
                summaryBuilder.dateTimeAnswerSamples(buildDateTimeAnswerSamples(allAnswers, page, size));
                break;
                
            case FILE_UPLOAD:
                summaryBuilder.fileUploadCount(totalAnswers);
                break;
        }
        
        return summaryBuilder.build();
    }
    
    private List<FormSummaryResponse.TextAnswerSample> buildTextAnswerSamples(List<Answer> answers, int page, int size) {
        // Get paginated text answers
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, answers.size());
        
        if (startIndex >= answers.size()) {
            return new ArrayList<>();
        }
        
        return answers.subList(startIndex, endIndex).stream()
                .map(answer -> FormSummaryResponse.TextAnswerSample.builder()
                        .answerText(answer.getAnswerText())
                        .count(1L)
                        .percentage("100%") // Since we're showing individual answers
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<FormSummaryResponse.ChoiceOptionStats> buildChoiceOptionStats(List<Answer> answers) {
        Map<String, Long> optionCounts = answers.stream()
                .filter(answer -> answer.getSelectedOption() != null)
                .collect(Collectors.groupingBy(
                        Answer::getSelectedOption,
                        Collectors.counting()
                ));
        
        long total = optionCounts.values().stream().mapToLong(Long::longValue).sum();
        
        return optionCounts.entrySet().stream()
                .map(entry -> {
                    double percentage = total > 0 ? (entry.getValue() * 100.0) / total : 0.0;
                    return FormSummaryResponse.ChoiceOptionStats.builder()
                            .option(entry.getKey())
                            .count(entry.getValue())
                            .percentage(String.format("%.1f%%", percentage))
                            .build();
                })
                .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
                .collect(Collectors.toList());
    }
    
    private List<FormSummaryResponse.ChoiceOptionStats> buildMultipleChoiceOptionStats(List<Answer> answers) {
        Map<String, Long> optionCounts = new HashMap<>();
        
        answers.stream()
                .filter(answer -> answer.getSelectedOptions() != null)
                .forEach(answer -> {
                    answer.getSelectedOptions().forEach(option -> {
                        optionCounts.put(option, optionCounts.getOrDefault(option, 0L) + 1);
                    });
                });
        
        long total = optionCounts.values().stream().mapToLong(Long::longValue).sum();
        
        return optionCounts.entrySet().stream()
                .map(entry -> {
                    double percentage = total > 0 ? (entry.getValue() * 100.0) / total : 0.0;
                    return FormSummaryResponse.ChoiceOptionStats.builder()
                            .option(entry.getKey())
                            .count(entry.getValue())
                            .percentage(String.format("%.1f%%", percentage))
                            .build();
                })
                .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
                .collect(Collectors.toList());
    }
    
    private Double calculateAverageRating(List<Answer> answers) {
        return answers.stream()
                .filter(answer -> answer.getSelectedRating() != null)
                .mapToInt(Answer::getSelectedRating)
                .average()
                .orElse(0.0);
    }
    
    private Map<Integer, Long> buildRatingDistribution(List<Answer> answers) {
        return answers.stream()
                .filter(answer -> answer.getSelectedRating() != null)
                .collect(Collectors.groupingBy(
                        Answer::getSelectedRating,
                        Collectors.counting()
                ));
    }
    
    private List<FormSummaryResponse.DateTimeAnswerSample> buildDateTimeAnswerSamples(List<Answer> answers, int page, int size) {
        // Get paginated date/time answers
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, answers.size());
        
        if (startIndex >= answers.size()) {
            return new ArrayList<>();
        }
        
        return answers.subList(startIndex, endIndex).stream()
                .map(answer -> {
                    String dateTimeValue = null;
                    if (answer.getSelectedDate() != null) {
                        dateTimeValue = answer.getSelectedDate().toString();
                    } else if (answer.getSelectedTime() != null) {
                        dateTimeValue = answer.getSelectedTime().toString();
                    }
                    
                    return FormSummaryResponse.DateTimeAnswerSample.builder()
                            .dateTimeValue(dateTimeValue)
                            .count(1L)
                            .percentage("100%") // Since we're showing individual answers
                            .build();
                })
                .collect(Collectors.toList());
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
                User user = userRepository.findById(UUID.fromString(submission.getSubmissionBy()))
                        .orElse(null);
                if (user != null) {
                    submissionByUser = SubmissionResponse.SubmissionByUser.builder()
                            .id(user.getId().toString())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .build();
                }
            } catch (Exception e) {
                // User not found or invalid UUID, leave as null
            }
        }

        return SubmissionResponse.builder()
                .id(submission.getId().toString())
                .formId(submission.getForm().getId().toString())
                .formTitle(submission.getForm().getTitle())
                .submittedAt(submission.getSubmittedAt())
                .submissionBy(submission.getSubmissionBy())
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
                            User user = userRepository.findById(UUID.fromString(submission.getSubmissionBy()))
                                    .orElse(null);
                            if (user != null) {
                                submissionByUser = SubmissionListResponse.SubmissionByUser.builder()
                                        .id(user.getId().toString())
                                        .fullName(user.getFullName())
                                        .email(user.getEmail())
                                        .build();
                            }
                        } catch (Exception e) {
                            // User not found or invalid UUID, leave as null
                        }
                    }
                    
                    return SubmissionListResponse.SubmissionSummary.builder()
                            .id(submission.getId().toString())
                            .formId(submission.getForm().getId().toString())
                            .formTitle(submission.getForm().getTitle())
                            .submittedAt(submission.getSubmittedAt())
                            .submissionBy(submission.getSubmissionBy())
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
