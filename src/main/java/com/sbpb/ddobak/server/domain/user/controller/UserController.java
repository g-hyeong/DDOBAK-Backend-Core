package com.sbpb.ddobak.server.domain.user.controller;

import com.sbpb.ddobak.server.common.response.ApiResponse;
import com.sbpb.ddobak.server.common.response.SuccessCode;
import com.sbpb.ddobak.server.domain.user.dto.CreateUserRequest;
import com.sbpb.ddobak.server.domain.user.dto.UserResponse;
import com.sbpb.ddobak.server.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 컨트롤러 (테스트용 간단 구현)
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 사용자 생성 (테스트용)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        log.info("Creating user request received for email: {}", request.getEmail());

        UserResponse response = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, SuccessCode.CREATED));
    }
} 