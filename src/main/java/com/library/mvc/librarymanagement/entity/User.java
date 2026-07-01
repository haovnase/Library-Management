package com.library.mvc.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "UserID", length = 4)
    private String id;

    @Column(name = "Username", nullable = false, unique = true)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Role", nullable = false)
    private String role;

    @Column(name = "FullName", nullable = false)
    private String fullName;

    @Column(name = "Status", nullable = false)
    private String status;
}
