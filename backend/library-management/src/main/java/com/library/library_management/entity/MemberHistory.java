package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private AppUser changedBy;


    @Column(name = "field_name", nullable = false)
    private String fieldName;


    @Column(name = "old_value")
    private String oldValue;


    @Column(name = "new_value")
    private String newValue;


    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}