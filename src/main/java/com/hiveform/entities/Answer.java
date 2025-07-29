package com.hiveform.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_text" , columnDefinition = "TEXT" , nullable = true)
    private String answerText;

    @Column(name = "selected_option" , nullable = true)
    private String selectedOption;

    @Column(name = "selected_options" , nullable = true)
    private List<String> selectedOptions;

    @Column(name = "file_url" , nullable = true)
    private String fileUrl;

    @Column(name = "selected_date" , nullable = true)
    private LocalDate selectedDate;

    @Column(name = "selected_time" , nullable = true)
    private LocalTime selectedTime;

    @Column(name = "selected_rating" , columnDefinition = "SMALLINT" , nullable = true)
    private Integer selectedRating;

}

