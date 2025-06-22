package com.sbpb.ddobak.server.domain.documentProcess.service.domain.impl;

import com.sbpb.ddobak.server.common.utils.IdGenerator;
import com.sbpb.ddobak.server.config.AwsProperties;
import com.sbpb.ddobak.server.domain.documentProcess.dto.ContractAnalysisRequest;
import com.sbpb.ddobak.server.domain.documentProcess.dto.ContractAnalysisResponse;
import com.sbpb.ddobak.server.domain.documentProcess.dto.AnalysisStatus;
import com.sbpb.ddobak.server.domain.documentProcess.dto.OcrResult;
import com.sbpb.ddobak.server.domain.documentProcess.dto.BedrockAnalysisResult;
import com.sbpb.ddobak.server.domain.documentProcess.dto.DdobakCommentary;
import com.sbpb.ddobak.server.domain.documentProcess.dto.ToxicClause;
import com.sbpb.ddobak.server.domain.documentProcess.exception.ContractExceptions;
import com.sbpb.ddobak.server.domain.documentProcess.repository.ContractRepository;
import com.sbpb.ddobak.server.domain.documentProcess.service.domain.ContractAnalysisService;
import com.sbpb.ddobak.server.infrastructure.aws.s3.S3ClientAdapter;
import com.sbpb.ddobak.server.infrastructure.aws.stepfunctions.StepFunctionsInvoker;
import com.sbpb.ddobak.server.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 계약서 분석 도메인 서비스 구현체 (Domain Layer)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ContractAnalysisServiceImpl implements ContractAnalysisService {

    private final S3ClientAdapter s3ClientAdapter;
    private final StepFunctionsInvoker stepFunctionsInvoker;
    private final ContractRepository contractRepository;
    private final AwsProperties awsProperties;

    // S3 설정
    private static final String S3_KEY_PREFIX = "contract/origin-images/";
    
    // 파일 검증 설정
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final int MAX_FILE_COUNT = 10; // 최대 10페이지
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png"
    );

    @Override
    @Transactional
    public ContractAnalysisResponse processAnalysis(ContractAnalysisRequest request, String userId) {
        log.info("Starting analysis process for user: {}", userId);

        // 1. 파일 목록 검증
        validateAnalysisFiles(request.getFiles(), request.getExpectedCount());

        // 2. Contract ID 생성
        String contractId = IdGenerator.generateContractId();
        log.info("Generated contract ID: {} for user: {}", contractId, userId);

        // 3. S3에 다중 이미지 업로드
        List<String> s3Keys = uploadMultipleImagesToS3(request.getFiles(), contractId);
        log.info("Images uploaded to S3: {} files for contract: {}", s3Keys.size(), contractId);

        // 4. AWS Step Functions 동기 호출 (시연용 - 결과까지 대기)
        ContractAnalysisResponse result = startAnalysisWorkflowSync(contractId, s3Keys, userId, request);

        // 5. 완료된 결과 반환
        log.info("Analysis workflow completed for contract: {}", contractId);
        return result;
    }

    @Override
    public void validateAnalysisFiles(List<MultipartFile> files, Integer expectedCount) {
        log.debug("Validating analysis files");

        // 파일 목록 존재 여부 확인
        if (files == null || files.isEmpty()) {
            log.warn("Analysis files validation failed: Files are missing or empty");
            throw ContractExceptions.AnalysisFileException.fileMissing();
        }

        // 파일 개수 확인
        if (files.size() > MAX_FILE_COUNT) {
            log.warn("Analysis files validation failed: File count {} exceeds limit {}", 
                    files.size(), MAX_FILE_COUNT);
            throw ContractExceptions.AnalysisFileException.fileTooLarge(); // 기존 예외 재사용
        }

        // 예상 개수와 실제 개수 비교
        if (expectedCount != null && !expectedCount.equals(files.size())) {
            log.warn("Analysis files validation failed: Expected count {} but received {}", 
                    expectedCount, files.size());
            // 경고 로그만 남기고 처리 계속 (비즈니스 요구사항에 따라 변경 가능)
        }

        // 각 파일 개별 검증
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            validateSingleAnalysisFile(file, i + 1);
        }

        log.debug("Analysis files validation passed: count={}", files.size());
    }

    /**
     * 개별 파일 검증
     */
    private void validateSingleAnalysisFile(MultipartFile file, int pageNumber) {
        // 파일 존재 여부 확인
        if (file == null || file.isEmpty()) {
            log.warn("Analysis file validation failed for page {}: File is missing or empty", pageNumber);
            throw ContractExceptions.AnalysisFileException.fileMissing();
        }

        // 파일 크기 확인
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("Analysis file validation failed for page {}: File size {} exceeds limit {}", 
                    pageNumber, file.getSize(), MAX_FILE_SIZE);
            throw ContractExceptions.AnalysisFileException.fileTooLarge();
        }

        // 파일 타입 확인
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Analysis file validation failed for page {}: Unsupported content type: {}", 
                    pageNumber, contentType);
            throw ContractExceptions.AnalysisFileException.unsupportedFileType(contentType);
        }

        log.debug("Analysis file validation passed for page {}: size={}, type={}", 
                pageNumber, file.getSize(), contentType);
    }

    /**
     * S3에 다중 이미지 업로드
     */
    private List<String> uploadMultipleImagesToS3(List<MultipartFile> files, String contractId) {
        List<String> s3Keys = new ArrayList<>();
        String serviceBucket = awsProperties.getS3().getServiceBucket();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String pageNumber = String.format("%03d", i + 1); // 001, 002, 003...
            String s3Key = S3_KEY_PREFIX + contractId + "/" + pageNumber + ".jpg";
            
            try {
                log.debug("Uploading page {} to S3 bucket: {} with key: {}", i + 1, serviceBucket, s3Key);
                
                boolean uploadSuccess = s3ClientAdapter.uploadObject(
                    serviceBucket, 
                    s3Key, 
                    file.getInputStream(), 
                    file.getSize()
                );

                if (!uploadSuccess) {
                    log.error("Failed to upload page {} to S3 for contract: {}", i + 1, contractId);
                    throw new RuntimeException("S3 upload failed for contract: " + contractId + ", page: " + (i + 1));
                }

                s3Keys.add(s3Key);
                log.debug("Successfully uploaded page {} with S3 key: {}", i + 1, s3Key);

            } catch (IOException e) {
                log.error("IOException occurred while uploading page {} to S3 for contract: {}", i + 1, contractId, e);
                throw new RuntimeException("Failed to read file for S3 upload", e);
            }
        }

        log.info("All {} files uploaded successfully for contract: {}", files.size(), contractId);
        return s3Keys;
    }

    /**
     * AWS Step Functions를 사용한 분석 워크플로우 시작 (동기 - 시연용)
     * 
     * TODO: 시연 완료 후 비동기 방식으로 되돌리기
     * - startAnalysisWorkflow 메서드의 주석 해제
     * - 이 메서드 제거 또는 주석 처리
     * - processAnalysis에서 ContractAnalysisResponse.createSimpleResponse 사용
     */
    private ContractAnalysisResponse startAnalysisWorkflowSync(String contractId, List<String> s3Keys, String userId, ContractAnalysisRequest request) {
        log.info("Starting SYNC analysis workflow for contract: {} with {} files", contractId, s3Keys.size());

        try {
            // Step Functions 입력 데이터 구성
            Map<String, Object> workflowInput = createWorkflowInput(contractId, s3Keys, userId, request);

            // Step Functions 동기 실행 (완료까지 대기)
            ApiResponse<Map<String, Object>> response = stepFunctionsInvoker.startSyncExecution("contract_analysis", workflowInput);

            if (response.isSuccess()) {
                Map<String, Object> responseData = response.getData();
                String executionArn = (String) responseData.get("executionArn");
                Long executionTime = (Long) responseData.get("executionTime");
                
                log.info("Step Functions sync execution completed successfully - Contract: {}, ExecutionArn: {}, Time: {}ms", 
                        contractId, executionArn, executionTime);
                
                // Step Functions 출력에서 결과 파싱
                @SuppressWarnings("unchecked")
                Map<String, Object> stepFunctionsOutput = (Map<String, Object>) responseData.get("output");
                
                if (stepFunctionsOutput != null) {
                    // 실제 분석 결과를 포함한 응답 생성
                    return parseStepFunctionsResult(stepFunctionsOutput);
                } else {
                    log.warn("No output data received from Step Functions for contract: {}", contractId);
                    // 출력이 없는 경우 기본 응답 반환
                    return ContractAnalysisResponse.createSimpleResponse(
                            contractId, 
                            request.getClientId(), 
                            request.getClientToken(), 
                            request.getExpectedCount()
                    );
                }
                
            } else {
                log.error("Failed to execute Step Functions sync workflow - Contract: {}, Error: {}", contractId, response.getMessage());
                throw new RuntimeException("Failed to complete analysis workflow for contract: " + contractId);
            }

        } catch (Exception e) {
            log.error("Error executing sync analysis workflow for contract: {}", contractId, e);
            throw new RuntimeException("Failed to complete analysis workflow", e);
        }
    }

    /**
     * AWS Step Functions를 사용한 분석 워크플로우 시작 (비동기 - 운영용)
     * 
     * TODO: 시연 완료 후 이 메서드 활성화하여 비동기로 복원
     * 복원 방법:
     * 1. 이 주석 블록 해제
     * 2. startAnalysisWorkflowSync 메서드 제거 또는 주석 처리
     * 3. processAnalysis에서 다음과 같이 변경:
     *    - startAnalysisWorkflowSync() 호출을 startAnalysisWorkflow() 호출로 변경
     *    - 반환을 ContractAnalysisResponse.createSimpleResponse()로 변경
     */
    /*
    private void startAnalysisWorkflow(String contractId, List<String> s3Keys, String userId, ContractAnalysisRequest request) {
        log.info("Starting analysis workflow for contract: {} with {} files", contractId, s3Keys.size());

        try {
            // Step Functions 입력 데이터 구성
            Map<String, Object> workflowInput = createWorkflowInput(contractId, s3Keys, userId, request);

            // Step Functions 비동기 실행 시작
            ApiResponse<Map<String, Object>> response = stepFunctionsInvoker.startExecution("contract_analysis", workflowInput);

            if (response.isSuccess()) {
                String executionArn = (String) response.getData().get("executionArn");
                log.info("Step Functions execution started successfully - Contract: {}, ExecutionArn: {}", contractId, executionArn);
                
                // TODO: executionArn을 별도 테이블에 저장하여 추후 상태 조회 가능하게 할 수 있음
                // workflowExecutionRepository.save(WorkflowExecution.of(contractId, executionArn, userId));
                
            } else {
                log.error("Failed to start Step Functions execution - Contract: {}, Error: {}", contractId, response.getMessage());
                throw new RuntimeException("Failed to start analysis workflow for contract: " + contractId);
            }

        } catch (Exception e) {
            log.error("Error starting analysis workflow for contract: {}", contractId, e);
            throw new RuntimeException("Failed to initiate analysis workflow", e);
        }
    }
    */

    /**
     * Step Functions 워크플로우 입력 데이터 생성 (새로운 형식)
     */
    private Map<String, Object> createWorkflowInput(String contractId, List<String> s3Keys, String userId, ContractAnalysisRequest request) {
        Map<String, Object> input = new HashMap<>();
        
        // 기본 정보 (새로운 형식에 맞춰 수정)
        input.put("contractId", contractId);
        input.put("s3Keys", s3Keys);
        input.put("clientId", request.getClientId() != null ? request.getClientId() : userId); // clientId가 없으면 userId 사용
        input.put("clientToken", request.getClientToken());
        input.put("expectedCount", request.getExpectedCount() != null ? request.getExpectedCount() : s3Keys.size());
        
        log.debug("Created workflow input for contract: {} - s3Keys count: {}, clientId: {}", 
                contractId, s3Keys.size(), input.get("clientId"));
        log.debug("Step Functions input: {}", input);
        
        return input;
    }

    // ===== 향후 비동기 확장용 메서드 구현 =====
    
    @Override
    public AnalysisStatus getAnalysisStatus(String contractId) {
        log.info("Getting analysis status for contract: {}", contractId);
        
        // TODO: 실제 구현 시에는 다음 로직을 사용
        // 1. WorkflowExecution 테이블에서 executionArn 조회
        // 2. Step Functions describeExecution 호출
        // 3. 상태에 따라 AnalysisStatus 매핑 반환
        
        // 현재는 기본 상태 반환 (개발 완료 후 제거)
        log.warn("getAnalysisStatus not fully implemented yet - returning IN_PROGRESS for contract: {}", contractId);
        return AnalysisStatus.IN_PROGRESS;
    }
    
    @Override
    public ContractAnalysisResponse parseStepFunctionsResult(Map<String, Object> stepFunctionsOutput) {
        log.info("Parsing Step Functions result");
        
        try {
            // Step Functions 출력에서 필요한 데이터 추출
            String contractId = (String) stepFunctionsOutput.get("contractId");
            @SuppressWarnings("unchecked")
            List<String> s3Keys = (List<String>) stepFunctionsOutput.get("s3Keys");
            String clientId = (String) stepFunctionsOutput.get("clientId");
            String clientToken = (String) stepFunctionsOutput.get("clientToken");
            Integer expectedCount = (Integer) stepFunctionsOutput.get("expectedCount");
            
            // OCR 결과 파싱
            List<OcrResult> ocrResults = parseOcrResults(stepFunctionsOutput);
            
            // Bedrock 분석 결과 파싱
            BedrockAnalysisResult bedrockResults = parseBedrockResults(stepFunctionsOutput);
            
            ContractAnalysisResponse response = ContractAnalysisResponse.builder()
                    .contractId(contractId)
                    .s3Keys(s3Keys)
                    .clientId(clientId)
                    .clientToken(clientToken)
                    .expectedCount(expectedCount)
                    .ocrResults(ocrResults)
                    .bedrockResults(bedrockResults)
                    .build();
                    
            log.info("Successfully parsed Step Functions result for contract: {}", contractId);
            return response;
            
        } catch (Exception e) {
            log.error("Failed to parse Step Functions result: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse analysis result", e);
        }
    }
    
    /**
     * OCR 결과 파싱
     */
    private List<OcrResult> parseOcrResults(Map<String, Object> stepFunctionsOutput) {
        List<OcrResult> ocrResults = new ArrayList<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> ocrData = (List<Map<String, Object>>) stepFunctionsOutput.get("ocrResults");
            
            if (ocrData != null) {
                for (Map<String, Object> ocr : ocrData) {
                    OcrResult ocrResult = OcrResult.builder()
                            .page((Integer) ocr.get("page"))
                            .text((String) ocr.get("text"))
                            .s3Key((String) ocr.get("s3Key"))
                            .build();
                    ocrResults.add(ocrResult);
                }
            }
            
            log.debug("Parsed {} OCR results", ocrResults.size());
            
        } catch (Exception e) {
            log.error("Failed to parse OCR results: {}", e.getMessage(), e);
        }
        
        return ocrResults;
    }
    
    /**
     * Bedrock 분석 결과 파싱
     */
    private BedrockAnalysisResult parseBedrockResults(Map<String, Object> stepFunctionsOutput) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> bedrockData = (Map<String, Object>) stepFunctionsOutput.get("bedrockResults");
            
            if (bedrockData == null) {
                log.warn("No bedrock results found in Step Functions output");
                return null;
            }
            
            // DdobakCommentary 파싱
            DdobakCommentary commentary = parseDdobakCommentary(bedrockData);
            
            // ToxicClause 목록 파싱
            List<ToxicClause> toxicClauses = parseToxicClauses(bedrockData);
            
            BedrockAnalysisResult bedrockResult = BedrockAnalysisResult.builder()
                    .originContent((String) bedrockData.get("originContent"))
                    .summary((String) bedrockData.get("summary"))
                    .ddobakCommentary(commentary)
                    .toxics(toxicClauses)
                    .build();
                    
            log.debug("Successfully parsed Bedrock analysis results");
            return bedrockResult;
            
        } catch (Exception e) {
            log.error("Failed to parse Bedrock results: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 또박이 코멘터리 파싱
     */
    private DdobakCommentary parseDdobakCommentary(Map<String, Object> bedrockData) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> commentaryData = (Map<String, Object>) bedrockData.get("ddobakCommentary");
            
            if (commentaryData == null) {
                return null;
            }
            
            return DdobakCommentary.builder()
                    .overallComment((String) commentaryData.get("overallComment"))
                    .warningComment((String) commentaryData.get("warningComment"))
                    .advice((String) commentaryData.get("advice"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to parse DdobakCommentary: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 독소 조항 목록 파싱
     */
    private List<ToxicClause> parseToxicClauses(Map<String, Object> bedrockData) {
        List<ToxicClause> toxicClauses = new ArrayList<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> toxicsData = (List<Map<String, Object>>) bedrockData.get("toxics");
            
            if (toxicsData != null) {
                for (Map<String, Object> toxic : toxicsData) {
                    ToxicClause toxicClause = ToxicClause.builder()
                            .title((String) toxic.get("title"))
                            .clause((String) toxic.get("clause"))
                            .reason((String) toxic.get("reason"))
                            .reasonReference((String) toxic.get("reasonReference"))
                            .warnLevel((Integer) toxic.get("warnLevel"))
                            .build();
                    toxicClauses.add(toxicClause);
                }
            }
            
            log.debug("Parsed {} toxic clauses", toxicClauses.size());
            
        } catch (Exception e) {
            log.error("Failed to parse toxic clauses: {}", e.getMessage(), e);
        }
        
        return toxicClauses;
    }
}