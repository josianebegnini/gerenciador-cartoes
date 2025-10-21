package com.ms_auth.domain.service;

import com.ms_auth.domain.entity.User;

import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User validateUser(String username, String password);
    Optional<User> findByUsername(String username);
}
