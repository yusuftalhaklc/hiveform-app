package com.hiveform.entities;

import java.util.List;
import java.util.UUID;

import com.hiveform.enums.QuestionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "title", columnDefinition = "TEXT", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT", nullable = true)
    private String description;
    
    @Column(name = "question_index", nullable = false)
    private Integer questionIndex;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    @Column(name = "options")
    private List<String> options;
    
    @Column(name = "created_at")
    private Long createdAt;
    
    @Column(name = "updated_at")
    private Long updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @PrePersist
    protected void onCreate() {
        long timestamp = System.currentTimeMillis() / 1000;
        createdAt = timestamp;
        updatedAt = timestamp;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis() / 1000;
    }
}
