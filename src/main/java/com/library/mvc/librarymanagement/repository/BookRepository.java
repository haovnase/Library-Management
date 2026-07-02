package com.library.mvc.librarymanagement.repository;

import com.library.mvc.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    Optional<Book> findByIsbn(String isbn);

    List<Book> findByBookNameContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            String bookName,
            String author,
            String isbn);
}
