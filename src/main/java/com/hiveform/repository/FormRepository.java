package com.hiveform.repository;

import com.hiveform.entities.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormRepository extends JpaRepository<Form, UUID> {

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Form f WHERE f.shortLink = ?1")
    boolean existsByShortLink(String shortLink);
    

    @Query("SELECT f FROM Form f WHERE f.shortLink = ?1")
    Optional<Form> findByShortLink(String shortLink);

    @Query("SELECT f FROM Form f WHERE f.user.id = ?1 ORDER BY f.createdAt DESC")
    Page<Form> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
