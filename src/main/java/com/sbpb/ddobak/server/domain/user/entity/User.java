package com.sbpb.ddobak.server.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = true)
    private String nickname;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Builder
    public User(String id, String email, String name, String nickname, 
                LocalDateTime createdAt, LocalDateTime lastLoginAt, Boolean isDeleted) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.isDeleted = isDeleted;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    /**
     * 마지막 로그인 시간 업데이트
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 사용자 삭제 처리 (소프트 삭제)
     */
    public void delete() {
        this.isDeleted = true;
    }
} 