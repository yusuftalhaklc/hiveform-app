package com.hiveform.repository;

import com.hiveform.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    
    List<Answer> findBySubmissionId(UUID submissionId);
    
    @Modifying
    @Query("DELETE FROM Answer a WHERE a.submission.id = ?1")
    void deleteBySubmissionId(UUID submissionId);
}
