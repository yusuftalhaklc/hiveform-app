package com.hiveform.repository;

import com.hiveform.entities.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    
    List<Answer> findBySubmissionId(UUID submissionId);
    
    List<Answer> findByQuestionId(UUID questionId);
    
    Page<Answer> findByQuestionIdOrderBySubmissionSubmittedAtDesc(UUID questionId, Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM Answer a WHERE a.submission.id = ?1")
    void deleteBySubmissionId(UUID submissionId);
}
