package com.sbpb.ddobak.server.infrastructure.aws.config;

import com.sbpb.ddobak.server.infrastructure.aws.client.AwsClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.Map;

/**
 * AWS Infrastructure 설정 클래스
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AwsInfrastructureConfig {

    private final AwsClientFactory awsClientFactory;

    /**
     * AWS Lambda Client Bean 등록
     */
    @Bean
    public LambdaClient lambdaClient() {
        return awsClientFactory.createLambdaClient();
    }

    /**
     * AWS S3 Client Bean 등록
     */
    @Bean
    public S3Client s3Client() {
        return awsClientFactory.createS3Client();
    }

    /**
     * 운영용 Lambda 함수 설정 맵
     * 테스트용 설정은 별도 테스트 설정 클래스에서 관리
     */
    @Bean
    public Map<String, LambdaFunctionSpec> productionLambdaFunctions() {
        log.info("Initializing production Lambda function configurations");
        
        Map<String, LambdaFunctionSpec> functions = new HashMap<>();
        
        // OCR Lambda 설정 - 문서 OCR 처리용 비동기 함수
        functions.put("ocr_lambda", LambdaFunctionSpec.builder()
                .functionName("ocr_lambda")
                .description("문서 OCR 처리 Lambda 함수 (운영용)")
                .defaultInvocationType("Event") // 비동기 호출
                .timeoutMillis(30000L) // 30초 - OCR 처리 시간 고려
                .validateResponseSchema(true) // 응답 스키마 검증 활성화
                .maxRetries(2) // 재시도 2회 - OCR 처리 실패 시 재시도
                .collectLogs(true) // 로그 수집 활성화 - 운영 디버깅용
                .build());
        
        log.info("Production Lambda function configurations initialized: {}", functions.keySet());
        return functions;
    }

    /**
     * Lambda 함수 스펙 정의
     * 기존 LambdaFunctionConfig를 간소화한 버전
     */
    public static class LambdaFunctionSpec {
        private String functionName;
        private String description;
        private String defaultInvocationType;
        private Long timeoutMillis;
        private boolean validateResponseSchema;
        private int maxRetries;
        private boolean collectLogs;

        // Builder 패턴
        public static LambdaFunctionSpecBuilder builder() {
            return new LambdaFunctionSpecBuilder();
        }

        // Getters
        public String getFunctionName() { return functionName; }
        public String getDescription() { return description; }
        public String getDefaultInvocationType() { return defaultInvocationType; }
        public Long getTimeoutMillis() { return timeoutMillis; }
        public boolean isValidateResponseSchema() { return validateResponseSchema; }
        public int getMaxRetries() { return maxRetries; }
        public boolean isCollectLogs() { return collectLogs; }

        // Builder 클래스
        public static class LambdaFunctionSpecBuilder {
            private String functionName;
            private String description;
            private String defaultInvocationType;
            private Long timeoutMillis;
            private boolean validateResponseSchema;
            private int maxRetries;
            private boolean collectLogs;

            public LambdaFunctionSpecBuilder functionName(String functionName) {
                this.functionName = functionName;
                return this;
            }

            public LambdaFunctionSpecBuilder description(String description) {
                this.description = description;
                return this;
            }

            public LambdaFunctionSpecBuilder defaultInvocationType(String defaultInvocationType) {
                this.defaultInvocationType = defaultInvocationType;
                return this;
            }

            public LambdaFunctionSpecBuilder timeoutMillis(Long timeoutMillis) {
                this.timeoutMillis = timeoutMillis;
                return this;
            }

            public LambdaFunctionSpecBuilder validateResponseSchema(boolean validateResponseSchema) {
                this.validateResponseSchema = validateResponseSchema;
                return this;
            }

            public LambdaFunctionSpecBuilder maxRetries(int maxRetries) {
                this.maxRetries = maxRetries;
                return this;
            }

            public LambdaFunctionSpecBuilder collectLogs(boolean collectLogs) {
                this.collectLogs = collectLogs;
                return this;
            }

            public LambdaFunctionSpec build() {
                LambdaFunctionSpec spec = new LambdaFunctionSpec();
                spec.functionName = this.functionName;
                spec.description = this.description;
                spec.defaultInvocationType = this.defaultInvocationType;
                spec.timeoutMillis = this.timeoutMillis;
                spec.validateResponseSchema = this.validateResponseSchema;
                spec.maxRetries = this.maxRetries;
                spec.collectLogs = this.collectLogs;
                return spec;
            }
        }
    }
} 