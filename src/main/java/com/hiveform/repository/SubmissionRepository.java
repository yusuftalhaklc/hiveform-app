package com.hiveform.repository;

import com.hiveform.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.form.id = ?1")
    Long countByFormId(UUID formId);
}
