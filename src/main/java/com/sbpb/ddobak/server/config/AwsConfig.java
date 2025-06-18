package com.sbpb.ddobak.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS 관련 설정을 관리하는 Configuration 클래스
 * AWSProperties를 통해 설정값을 주입받아 AWS 서비스 Bean 생성
 */
@Configuration
@RequiredArgsConstructor
public class AwsConfig {

    private final AwsProperties awsProperties;

    /**
     * S3 클라이언트 Bean 생성
     * AWS Properties에서 설정된 프로필과 리전을 사용하여 인증 처리
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(
                        ProfileCredentialsProvider.builder()
                                .profileName(awsProperties.getProfile())
                                .build()
                )
                .build();
    }
} 