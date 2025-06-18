package com.sbpb.ddobak.server.domain.documentProcess.exception;

/**
 * Contract 관련 예외들
 */
public class ContractExceptions {

    /**
     * 계약서를 찾을 수 없는 경우
     */
    public static class ContractNotFoundException extends DocumentProcessBusinessException {
        public ContractNotFoundException(String contractId) {
            super(DocumentProcessErrorCode.CONTRACT_NOT_FOUND, "Contract not found with ID: " + contractId);
        }

        public static ContractNotFoundException withId(String contractId) {
            return new ContractNotFoundException(contractId);
        }
    }

    /**
     * 권한 없는 계약서 접근
     */
    public static class UnauthorizedContractAccessException extends DocumentProcessBusinessException {
        public UnauthorizedContractAccessException(String contractId) {
            super(DocumentProcessErrorCode.UNAUTHORIZED_CONTRACT_ACCESS, "Unauthorized access to contract: " + contractId);
        }

        public static UnauthorizedContractAccessException forContract(String contractId) {
            return new UnauthorizedContractAccessException(contractId);
        }
    }

    /**
     * OCR 파일 관련 예외들
     */
    public static class OcrFileException extends DocumentProcessBusinessException {
        private OcrFileException(DocumentProcessErrorCode errorCode, String message) {
            super(errorCode, message);
        }

        public static OcrFileException fileMissing() {
            return new OcrFileException(DocumentProcessErrorCode.OCR_FILE_MISSING, "File is required for OCR processing");
        }

        public static OcrFileException fileTooLarge() {
            return new OcrFileException(DocumentProcessErrorCode.OCR_FILE_TOO_LARGE, "File size exceeds 10MB limit");
        }

        public static OcrFileException unsupportedFileType(String contentType) {
            return new OcrFileException(DocumentProcessErrorCode.OCR_UNSUPPORTED_FILE_TYPE, "Unsupported file type: " + contentType);
        }
    }

    /**
     * OCR 결과를 찾을 수 없는 경우
     */
    public static class OcrResultNotFoundException extends DocumentProcessBusinessException {
        public OcrResultNotFoundException(String contractId) {
            super(DocumentProcessErrorCode.OCR_RESULT_NOT_FOUND, "OCR result not found for contract ID: " + contractId);
        }

        public static OcrResultNotFoundException forContract(String contractId) {
            return new OcrResultNotFoundException(contractId);
        }
    }
} 