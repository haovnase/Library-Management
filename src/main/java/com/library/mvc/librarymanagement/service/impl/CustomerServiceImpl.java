package com.library.mvc.librarymanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.repository.CustomerRepository;
import com.library.mvc.librarymanagement.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer findByUserId(String userId) {
        return customerRepository.findByUserId(userId);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public String generateNextCustomerId() {

        int max = customerRepository.findAll().stream()
                .map(Customer::getId)
                .filter(id -> id.startsWith("C"))
                .map(id -> id.substring(1))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("C%03d", max + 1);
    }
}