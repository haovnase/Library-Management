package com.library.mvc.librarymanagement.service;

import java.util.List;

import com.library.mvc.librarymanagement.entity.BorrowBook;
import com.library.mvc.librarymanagement.entity.Customer;

public interface BorrowBookService {

    List<BorrowBook> findByCustomer(Customer customer);

    BorrowBook findById(String id);

    BorrowBook save(BorrowBook borrowBook);

    void renewBorrow(String id);

}