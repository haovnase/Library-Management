package com.library.mvc.librarymanagement.repository;

import com.library.mvc.librarymanagement.entity.BorrowBook;
import com.library.mvc.librarymanagement.entity.Customer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowBookRepository extends JpaRepository<BorrowBook, String> {

    List<BorrowBook> findByCustomerAndStatus(Customer customer, String string);

    Object countByStatus(String string);

    Object countByStatusAndDeadlineBefore(String string, LocalDate now);

    List<BorrowBook> findTop10ByOrderByBorrowDateDesc();

    List<BorrowBook> findByCustomer(Customer customer);

    
}
