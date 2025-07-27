package com.hiveform.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hiveform.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
