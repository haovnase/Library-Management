package com.library.mvc.librarymanagement.service;

import java.util.List;

import com.library.mvc.librarymanagement.entity.Book;

public interface BookService {

    List<Book> findAll();

    List<Book> search(String keyword);

    Book findById(String id);

}