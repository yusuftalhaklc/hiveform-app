package com.hiveform.repository;

import com.hiveform.entities.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormRepository extends JpaRepository<Form, UUID> {

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Form f WHERE f.shortLink = ?1")
    boolean existsByShortLink(String shortLink);
    

    @Query("SELECT f FROM Form f WHERE f.shortLink = ?1")
    Optional<Form> findByShortLink(String shortLink);
}
