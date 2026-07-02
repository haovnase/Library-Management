package com.library.mvc.librarymanagement.repository;

import com.library.mvc.librarymanagement.entity.Book;
import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    
    Customer findByUserId(String userId);
}
