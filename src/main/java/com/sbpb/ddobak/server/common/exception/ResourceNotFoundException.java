package com.sbpb.ddobak.server.common.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 * 
 * 사용 시나리오:
 * - 데이터베이스에서 특정 ID로 엔티티 조회 실패
 * - 파일이나 외부 리소스 접근 실패
 * - API 엔드포인트에서 요청한 리소스가 존재하지 않음
 */
public class ResourceNotFoundException extends BusinessException {

    /**
     * 기본 리소스 없음 예외
     * 
     * @param message 예외 메시지
     */
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    /**
     * 리소스 타입과 식별자를 명시한 예외
     * 
     * @param resourceType 리소스 타입 (예: "User", "Document", "Content")
     * @param identifier   식별자 (ID, 이메일, 이름 등)
     */
    public ResourceNotFoundException(String resourceType, Object identifier) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
                String.format("%s not found with identifier: %s", resourceType, identifier));
        addProperty("resourceType", resourceType);
        addProperty("identifier", identifier);
    }

    /**
     * 특정 필드로 리소스를 찾을 수 없는 경우
     * 
     * @param resourceType 리소스 타입
     * @param fieldName    검색 필드명
     * @param fieldValue   검색 필드값
     */
    public ResourceNotFoundException(String resourceType, String fieldName, Object fieldValue) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
                String.format("%s not found with %s: %s", resourceType, fieldName, fieldValue));
        addProperty("resourceType", resourceType);
        addProperty("fieldName", fieldName);
        addProperty("fieldValue", fieldValue);
    }

    /**
     * 사용자 리소스 없음 예외 (편의 메서드)
     * 
     * @param userId 사용자 ID
     */
    public static ResourceNotFoundException user(Long userId) {
        return new ResourceNotFoundException("User", "id", userId);
    }

    /**
     * 이메일로 사용자를 찾을 수 없는 경우 (편의 메서드)
     * 
     * @param email 이메일 주소
     */
    public static ResourceNotFoundException userByEmail(String email) {
        return new ResourceNotFoundException("User", "email", email);
    }

    /**
     * 문서 리소스 없음 예외 (편의 메서드)
     * 
     * @param documentId 문서 ID
     */
    public static ResourceNotFoundException document(Long documentId) {
        return new ResourceNotFoundException("Document", "id", documentId);
    }

    /**
     * 외부 컨텐츠 리소스 없음 예외 (편의 메서드)
     * 
     * @param contentId 컨텐츠 ID
     */
    public static ResourceNotFoundException externalContent(Long contentId) {
        return new ResourceNotFoundException("External Content", "id", contentId);
    }

    /**
     * 파일 리소스 없음 예외 (편의 메서드)
     * 
     * @param fileName 파일명
     */
    public static ResourceNotFoundException file(String fileName) {
        return new ResourceNotFoundException("File", "name", fileName);
    }
}