package com.sbpb.ddobak.server.infrastructure.aws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbpb.ddobak.server.common.exception.ExternalServiceException;
import com.sbpb.ddobak.server.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.util.Map;

/**
 * AWS Lambda 호출을 담당하는 클래스.
 */
@Component
@RequiredArgsConstructor
public class Lambda {

    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.lambda.function-name}")
    private String functionName;

    /**
     * 주어진 S3 키를 Lambda 함수로 전달하여 OCR 결과를 가져온다.
     *
     * @param key S3 object key
     * @return Lambda로부터 받은 ApiResponse
     */
    public ApiResponse<Object> requestLambda(String key) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of("s3Key", key));
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            String responseJson = lambdaClient.invoke(request).payload().asUtf8String();
            return objectMapper.readValue(responseJson, new TypeReference<ApiResponse<Object>>() {});
        } catch (Exception e) {
            throw new ExternalServiceException("Lambda", e.getMessage());
        }
    }
}
