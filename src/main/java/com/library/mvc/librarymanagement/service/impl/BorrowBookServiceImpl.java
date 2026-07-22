package com.library.mvc.librarymanagement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.mvc.librarymanagement.entity.BorrowBook;
import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.repository.BorrowBookRepository;
import com.library.mvc.librarymanagement.service.BorrowBookService;

@Service
public class BorrowBookServiceImpl implements BorrowBookService {

    @Autowired
    private BorrowBookRepository borrowBookRepository;

    @Override
    public List<BorrowBook> findByCustomer(Customer customer) {
        return borrowBookRepository.findByCustomer(customer);
    }

    @Override
    public BorrowBook findById(String id) {
        return borrowBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow not found"));
    }

    @Override
    public BorrowBook save(BorrowBook borrowBook) {
        return borrowBookRepository.save(borrowBook);
    }
    
    @Override
    public void renewBorrow(String id) {

        BorrowBook borrowBook = borrowBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow not found: " + id));

        borrowBook.setDeadline(borrowBook.getDeadline().plusDays(7));
        borrowBook.setDelay(borrowBook.getDelay() + 7);

        borrowBookRepository.save(borrowBook);
    }
}