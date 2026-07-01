package com.library.mvc.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @Column(name = "NotificationID", length = 4)
    private String id;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Content")
    private String content;

    @Column(name = "IsRead")
    private Boolean isRead;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
}
