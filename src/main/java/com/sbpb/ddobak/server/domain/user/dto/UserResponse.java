package com.sbpb.ddobak.server.domain.user.dto;

import com.sbpb.ddobak.server.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String email;
    private String name;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Boolean isDeleted;

    /**
     * User 엔티티를 UserResponse로 변환
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .nickname(user.getNickname())
            .createdAt(user.getCreatedAt())
            .lastLoginAt(user.getLastLoginAt())
            .isDeleted(user.getIsDeleted())
            .build();
    }
} 