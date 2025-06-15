package com.sbpb.ddobak.server.domain.documentProcess.service;

import com.sbpb.ddobak.server.common.response.ApiResponse;
import com.sbpb.ddobak.server.infrastructure.aws.Lambda;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OCR 처리를 담당하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OcrService {

    private final Lambda lambda;

    /**
     * 주어진 S3 경로를 Lambda로 전달하여 OCR 결과를 받아온다.
     *
     * @param s3Key S3 object key
     * @return Lambda로부터 받은 ApiResponse
     */
    public ApiResponse<Object> requestOcr(String s3Key) {
        log.info("Requesting OCR for key: {}", s3Key);
        return lambda.requestLambda(s3Key);
    }
}
