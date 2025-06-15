package com.sbpb.ddobak.server.domain.documentProcess.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OCR 요청 시 사용되는 DTO.
 */
@Getter
@NoArgsConstructor
public class OcrRequest {

    @NotBlank(message = "s3Key is required")
    private String s3Key;
}
