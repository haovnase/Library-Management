package com.library.mvc.librarymanagement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.PreOrderBook;
import com.library.mvc.librarymanagement.repository.PreOrderBookRepository;
import com.library.mvc.librarymanagement.service.PreOrderBookService;

@Service
public class PreOrderBookServiceImpl
        implements PreOrderBookService {

    @Autowired
    private PreOrderBookRepository preOrderBookRepository;

    @Override
    public List<PreOrderBook> findAll() {
        return preOrderBookRepository.findAll();
    }

    @Override
    public List<PreOrderBook> findByCustomer(Customer customer) {
        return preOrderBookRepository.findByCustomer(customer);
    }

    @Override
    public PreOrderBook findById(String id) {
        return preOrderBookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pre-order not found"));
    }

    @Override
    public PreOrderBook save(PreOrderBook preOrderBook) {
        return preOrderBookRepository.save(preOrderBook);
    }
}