# 🔧 DDOBAK API 개발 가이드

> **DDOBAK 프로젝트의 API 개발 시 준수해야 할 가이드라인**

이 문서는 DDOBAK 프로젝트에서 API를 개발할 때 `common` 모듈을 기반으로 일관된 Response와 Exception 처리를 위한 가이드입니다.

## 📤 Response 처리 방법

모든 API는 `ApiResponse<T>` 형식으로 통일된 응답을 제공해야 합니다.

### ✅ 성공 응답

```java
// Controller에서 사용 예시
@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        
        // 기본 성공 응답
        return ResponseEntity.ok(ApiResponse.success(user));
        
        // 커스텀 성공 코드 사용
        return ResponseEntity.ok(ApiResponse.success(user, SuccessCode.USER_RETRIEVED));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody CreateUserRequest request) {
        UserDto user = userService.create(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, SuccessCode.USER_CREATED));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        
        // 데이터 없는 성공 응답
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_DELETED));
    }
}
```

### 📋 페이징 응답

```java
@GetMapping("/users")
public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    Page<UserDto> userPage = userService.findAll(PageRequest.of(page, size));
    PageResponse<UserDto> pageResponse = PageResponse.from(userPage);
    
    return ResponseEntity.ok(ApiResponse.success(pageResponse));
}
```

### 📊 응답 형식 예시

#### 성공 응답
```json
{
  "success": true,
  "code": 2000,
  "message": "Request processed successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "timestamp": "2024-01-15T10:30:00",
  "trace_id": "a1b2c3d4e5f6g7h8"
}
```

#### 페이징 응답
```json
{
  "success": true,
  "code": 2000,
  "message": "Request processed successfully",
  "data": {
    "content": [
      {"id": 1, "name": "User 1"},
      {"id": 2, "name": "User 2"}
    ],
    "page_info": {
      "current_page": 0,
      "page_size": 20,
      "total_elements": 100,
      "total_pages": 5,
      "has_next": true,
      "has_previous": false
    }
  },
  "timestamp": "2024-01-15T10:30:00",
  "trace_id": "a1b2c3d4e5f6g7h8"
}
```

## ⚠️ Exception 처리 방법

비즈니스 로직에서 예외가 발생할 때는 common 모듈의 예외 클래스를 사용합니다.

### 🔍 리소스 없음 예외

```java
// Service에서 사용 예시
@Service
public class UserService {

    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.user(userId));
        
        return UserDto.from(user);
    }

    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.userByEmail(email));
        
        return UserDto.from(user);
    }
}
```

### 🔄 중복 리소스 예외

```java
@Service
public class UserService {

    public UserDto create(CreateUserRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw DuplicateResourceException.email(request.getEmail());
        }

        // 사용자명 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw DuplicateResourceException.username(request.getUsername());
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .build();

        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }
}
```

### ✅ 검증 예외

```java
@Service
public class UserService {

    public void updatePassword(Long userId, String newPassword) {
        // 비즈니스 규칙 검증
        if (newPassword.length() < 8) {
            throw new ValidationException("password", newPassword, 
                "Password must be at least 8 characters");
        }

        // 복잡한 비즈니스 규칙 위반
        if (!isValidPasswordPolicy(newPassword)) {
            throw ValidationException.businessRuleViolation(
                "PASSWORD_POLICY", 
                "Password must contain uppercase, lowercase, number and special character"
            );
        }

        // 패스워드 업데이트 로직...
    }
}
```

### 🌐 외부 서비스 예외

```java
@Service
public class ExternalApiService {

    public String fetchExternalData(String apiKey) {
        try {
            // 외부 API 호출...
            return externalApiClient.getData(apiKey);
        } catch (Exception e) {
            throw new ExternalServiceException("ExternalAPI", 
                "Failed to fetch data: " + e.getMessage());
        }
    }
}
```

### 📊 에러 응답 예시

```json
{
  "success": false,
  "code": 4300,
  "message": "User not found with id: 123",
  "timestamp": "2024-01-15T10:30:00",
  "trace_id": "a1b2c3d4e5f6g7h8"
}
```

## 📊 에러 코드 체계

프로젝트에서 사용하는 에러 코드는 다음과 같이 분류됩니다:

| 코드 범위 | 분류 | 예시 |
|----------|------|------|
| **2000-2099** | 일반 성공 | `2000: SUCCESS` |
| **2100-2199** | 생성 성공 | `2101: USER_CREATED` |
| **2200-2299** | 수정 성공 | `2201: USER_UPDATED` |
| **2300-2399** | 삭제 성공 | `2301: USER_DELETED` |
| **4000-4099** | 클라이언트 에러 | `4000: INVALID_INPUT` |
| **4100-4199** | 인증/인가 에러 | `4100: UNAUTHORIZED` |
| **4200-4299** | 비즈니스 로직 에러 | `4200: BUSINESS_RULE_VIOLATION` |
| **4300-4399** | 리소스 관련 에러 | `4300: RESOURCE_NOT_FOUND` |
| **5000-5099** | 서버 에러 | `5000: INTERNAL_SERVER_ERROR` |
| **5100-5199** | 데이터베이스 에러 | `5100: DATABASE_CONNECTION_ERROR` |
| **5200-5299** | 외부 서비스 에러 | `5200: EXTERNAL_SERVICE_UNAVAILABLE` |

## 🔧 편의 메서드 활용

Common 모듈에서 제공하는 편의 메서드를 적극 활용하세요:

### ResourceNotFoundException
```java
// 사용자 관련
ResourceNotFoundException.user(userId)
ResourceNotFoundException.userByEmail(email)

// 문서 관련
ResourceNotFoundException.document(documentId)

// 외부 컨텐츠 관련
ResourceNotFoundException.externalContent(contentId)

// 파일 관련
ResourceNotFoundException.file(fileName)
```

### DuplicateResourceException
```java
// 사용자 관련
DuplicateResourceException.email(email)
DuplicateResourceException.username(username)

// 문서 관련
DuplicateResourceException.documentName(documentName)

// 외부 컨텐츠 관련
DuplicateResourceException.contentUrl(url)

// 일반적인 키 중복
DuplicateResourceException.key(resourceType, key)
```

### ValidationException
```java
// 단일 필드 검증 실패
new ValidationException(field, value, reason)

// 비즈니스 규칙 위반
ValidationException.businessRuleViolation(ruleName, description)
```

## 🚫 금지 사항

### ❌ 하지 말아야 할 것들

```java
// ❌ 직접 ResponseEntity에 데이터만 담기
return ResponseEntity.ok(userData); 

// ❌ 일반 RuntimeException 사용
throw new RuntimeException("User not found");

// ❌ 한국어 에러 메시지 (시스템 메시지)
throw new ValidationException("사용자를 찾을 수 없습니다");

// ❌ HTTP 상태 코드만으로 에러 처리
return ResponseEntity.notFound().build();

// ❌ 매직 넘버 사용
return ResponseEntity.status(404).body("Not found");
```

### ✅ 올바른 방법

```java
// ✅ ApiResponse로 감싸서 응답
return ResponseEntity.ok(ApiResponse.success(userData));

// ✅ 구체적인 비즈니스 예외 사용
throw ResourceNotFoundException.user(userId);

// ✅ 영어 에러 메시지 (시스템 메시지)
throw new ValidationException("User not found");

// ✅ 구조화된 에러 응답
return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND));

// ✅ 의미있는 상수 사용
return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND));
```

## 🎯 개발 체크리스트

새로운 API를 개발할 때 다음 사항을 확인하세요:

### Controller 레이어
- [ ] `ResponseEntity<ApiResponse<T>>` 형식으로 응답
- [ ] 성공 시 적절한 `SuccessCode` 사용
- [ ] HTTP 상태 코드와 비즈니스 코드 일치성 확인

### Service 레이어
- [ ] 비즈니스 예외는 `common.exception` 패키지의 예외 클래스 사용
- [ ] 편의 메서드 활용 (예: `ResourceNotFoundException.user()`)
- [ ] 복잡한 검증 로직에서 `ValidationException` 사용

### 페이징 처리
- [ ] 페이징 응답 시 `PageResponse.from()` 사용
- [ ] 기본 페이지 크기 설정 (권장: 20)
- [ ] 최대 페이지 크기 제한 고려

### 메시지 처리
- [ ] 모든 시스템 메시지는 영어로 작성
- [ ] 로그 추적을 위한 추가 속성 설정 (필요시)
- [ ] 민감 정보 노출 방지

### 예외 처리
- [ ] 예외 발생 시 충분한 컨텍스트 정보 제공
- [ ] `addProperty()` 메서드로 디버깅 정보 추가
- [ ] 외부 서비스 호출 시 적절한 예외 래핑

## 📝 추가 참고 자료

- [Common 모듈 구조 분석](/docs/common-module-structure.md)
- [에러 코드 상세 가이드](/docs/error-codes.md)
- [API 테스트 가이드](/docs/api-testing-guide.md) 