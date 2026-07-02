package com.library.mvc.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BorrowBook")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBook {

    @Id
    @Column(name = "BorrowID", length = 5)
    private String id;

    @ManyToOne
    @JoinColumn(name = "BookID", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @Column(name = "BorrowDate", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "ReturnDate")
    private LocalDate returnDate;

    @Column(name = "Deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "Status")
    private String status;

    @Column(name = "Description")
    private String description;

    @Column(name = "Fine")
    private double fine;
}
