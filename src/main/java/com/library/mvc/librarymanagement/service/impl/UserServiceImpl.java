package com.library.mvc.librarymanagement.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.repository.UserRepository;
import com.library.mvc.librarymanagement.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public String generateNextUserId() {

        int max = userRepository.findAll().stream()
                .map(User::getId)
                .filter(id -> id.startsWith("U"))
                .map(id -> id.substring(1))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("U%03d", max + 1);
    }

    @Override
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
}