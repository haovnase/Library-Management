package com.library.mvc.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @Column(name = "BookID", length = 4)
    private String id;

    @Column(name = "BookName", nullable = false)
    private String bookName;

    @Column(name = "BookJacket")
    private String bookJacket;

    @Column(name = "Type")
    private String type;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "Author")
    private String author;

    @Column(name = "Publisher")
    private String publisher;

    @Column(name = "Language")
    private String language;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "Location")
    private String location;

    @Column(name = "Price")
    private BigDecimal price;

    @Column(name = "Year")
    private Integer year;
}
