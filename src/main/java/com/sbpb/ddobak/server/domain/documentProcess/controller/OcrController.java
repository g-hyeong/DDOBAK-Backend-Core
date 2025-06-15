package com.sbpb.ddobak.server.domain.documentProcess.controller;

import com.sbpb.ddobak.server.common.response.ApiResponse;
import com.sbpb.ddobak.server.domain.documentProcess.dto.OcrRequest;
import com.sbpb.ddobak.server.domain.documentProcess.service.OcrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OCR 요청을 처리하는 컨트롤러.
 */
@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
@Slf4j
public class OcrController {

    private final OcrService ocrService;

    /**
     * OCR 분석 요청을 처리한다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> analyze(@Valid @RequestBody OcrRequest request) {
        ApiResponse<Object> response = ocrService.requestOcr(request.getS3Key());
        return ResponseEntity.ok(response);
    }
}
