package com.library.mvc.librarymanagement.service;

import com.library.mvc.librarymanagement.entity.Customer;


public interface CustomerService {

    Customer findByUserId(String userId);

    Customer save(Customer customer);

    String generateNextCustomerId();

}