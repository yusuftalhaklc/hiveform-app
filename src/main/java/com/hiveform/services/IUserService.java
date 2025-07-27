package com.hiveform.services;

import com.hiveform.entities.User;
import java.util.UUID;

public interface IUserService {
    User createUser(User user);
    User updateUser(UUID id, User user);
    void deleteUser(UUID id);
}
