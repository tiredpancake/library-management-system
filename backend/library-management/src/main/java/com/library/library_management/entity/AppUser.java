package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;
}
