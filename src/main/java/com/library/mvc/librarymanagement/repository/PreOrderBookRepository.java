package com.library.mvc.librarymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.PreOrderBook;

@Repository
public interface PreOrderBookRepository
        extends JpaRepository<PreOrderBook, String> {

    List<PreOrderBook> findByCustomer(Customer customer);
}