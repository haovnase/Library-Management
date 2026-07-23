package com.library.mvc.librarymanagement.service;

import java.util.List;
import java.util.Optional;

import com.library.mvc.librarymanagement.entity.User;

public interface UserService {

    boolean existsByUsername(String username);

    User save(User user);

    String generateNextUserId();

    Optional<User> login(String memberId, String password);

    List<User> findAll();

    List<User> searchByUsername(String keyword);

    User findById(String id);

    void lockUser(String id);


}