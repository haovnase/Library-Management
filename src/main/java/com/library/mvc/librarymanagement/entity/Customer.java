package com.library.mvc.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(name = "CustomerID", length = 4)
    private String id;

    @OneToOne
    @JoinColumn(name = "UserID", nullable = false, unique = true)
    private User user;

    @Column(name = "Phone", length = 15)
    private String phone;
}