package com.library.mvc.librarymanagement.service;

import java.util.Optional;

import com.library.mvc.librarymanagement.entity.User;

public interface UserService {

    boolean existsByUsername(String username);

    User save(User user);

    String generateNextUserId();

    Optional<User> login(String memberId, String password);

}