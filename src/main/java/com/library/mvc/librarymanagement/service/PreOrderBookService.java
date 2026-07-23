package com.library.mvc.librarymanagement.service;

import java.util.List;

import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.PreOrderBook;

public interface PreOrderBookService {

    List<PreOrderBook> findAll();

    List<PreOrderBook> findByCustomer(Customer customer);

    PreOrderBook findById(String id);

    PreOrderBook save(PreOrderBook preOrderBook);
}