package com.library.mvc.librarymanagement.repository;

import com.library.mvc.librarymanagement.entity.BorrowBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowBookRepository extends JpaRepository<BorrowBook, String> {
}
