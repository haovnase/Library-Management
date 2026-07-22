package com.library.mvc.librarymanagement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.mvc.librarymanagement.entity.Book;
import com.library.mvc.librarymanagement.repository.BookRepository;
import com.library.mvc.librarymanagement.service.BookService;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return bookRepository.findAll();
        }

        return bookRepository
                .findByBookNameContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                        keyword, keyword, keyword);
    }

    @Override
    public Book findById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
}