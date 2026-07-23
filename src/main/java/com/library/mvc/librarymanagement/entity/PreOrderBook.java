package com.library.mvc.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "preorderbook")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreOrderBook {

    @Id
    @Column(name = "preorderID", length = 5)
    private String id;

    @ManyToOne
    @JoinColumn(name = "bookID", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "customerID", nullable = false)
    private Customer customer;

    @Column(name = "preorderdate", nullable = false)
    private LocalDate preOrderDate;

    @Column(name = "status", nullable = false)
    private String status;
}