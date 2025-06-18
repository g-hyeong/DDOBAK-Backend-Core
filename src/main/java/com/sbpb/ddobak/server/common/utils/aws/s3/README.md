# S3Util - AWS S3 유틸리티

## 📋 개요
AWS S3와 상호작용하기 위한 유틸리티 클래스입니다. 파일 업로드, 다운로드, 삭제, 목록 조회 등의 기본 기능을 제공합니다.

## 🚀 기능
- ✅ 파일 업로드 (`uploadObject`)
- ✅ 파일 다운로드 (`getObject`)
- ✅ 파일 삭제 (`deleteObject`)
- ✅ 객체 목록 조회 (`listObjects`)
- ✅ 객체 존재 확인 (`objectExists`)

## ⚙️ 설정

### 1. application.yml 설정
```yaml
aws:
  region: ap-northeast-2
  profile: ddobak
  s3:
    test-bucket: ddobak-test  # 테스트용 버킷
```

### 2. AWS 프로파일 설정
`~/.aws/credentials` 파일에 `ddobak` 프로파일 설정:
```ini
[ddobak]
aws_access_key_id = YOUR_ACCESS_KEY
aws_secret_access_key = YOUR_SECRET_KEY
```

## 🧪 테스트 실행

### 1. S3 통합 테스트 (순차적 파일 생성)
```bash
./gradlew testS3
```

**동작 방식:**
1. 테스트 버킷의 모든 객체 목록 조회
2. `test1`, `test2`, `test135` 등 기존 test 파일들 분석
3. 다음 번호 결정 (예: `test136`)
4. 새로운 test 파일 생성 및 업로드
5. 업로드 결과 검증

**예시 로그:**
```
=== S3 테스트 파일 순차 생성 시작 ===
Target bucket: ddobak-test
Step 1: 버킷 객체 목록 조회 중...
총 135개의 객체가 발견되었습니다
발견된 객체들:
  - test1
  - test2
  - test135
  - other-file.txt
Step 2: test 파일 패턴 분석 중...
기존 test 파일 중 최대 번호: 135
Step 3: 생성할 파일명 결정: test136
Step 4: 테스트 파일 생성 중...
✅ 파일 업로드 성공: test136
✅ 파일 존재 확인 완료: test136
=== S3 테스트 파일 순차 생성 완료 ===
생성된 파일: test136
파일 크기: 67 bytes
```

### 2. S3 테스트 파일 정리
```bash
./gradlew cleanS3
```

**동작 방식:**
- `test` 접두사를 가진 모든 파일 조회
- 각 test 파일 순차적 삭제
- 삭제 결과 로깅

## 📝 사용 예시

### 1. 파일 업로드
```java
@Autowired
private S3Util s3Util;

// 파일 업로드
InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
boolean success = s3Util.uploadObject("my-bucket", "path/file.txt", inputStream, content.length());
```

### 2. 파일 다운로드
```java
// 파일 다운로드
InputStream downloadedFile = s3Util.getObject("my-bucket", "path/file.txt");
```

### 3. 파일 목록 조회
```java
// 전체 목록 조회
List<String> allFiles = s3Util.listObjects("my-bucket", null);

// 특정 접두사로 필터링
List<String> testFiles = s3Util.listObjects("my-bucket", "test");
```

### 4. 파일 존재 확인
```java
// 파일 존재 여부 확인
boolean exists = s3Util.objectExists("my-bucket", "path/file.txt");
```

### 5. 파일 삭제
```java
// 파일 삭제
boolean deleted = s3Util.deleteObject("my-bucket", "path/file.txt");
```

## 🔧 개발자 가이드

### 에러 처리
- 모든 메서드는 예외를 잡아서 로깅하고 적절한 기본값 반환
- 업로드/삭제: `true`(성공) / `false`(실패)
- 다운로드: `InputStream` / `null`(실패)
- 목록 조회: `List<String>` / 빈 리스트(실패)

### 로깅 전략
- **성공 시**: INFO 레벨로 간단한 성공 메시지
- **실패 시**: ERROR 레벨로 상세한 오류 정보
- **디버그**: DEBUG 레벨로 세부 동작 추적

### 테스트 패턴 매칭
```java
private static final Pattern TEST_FILE_PATTERN = Pattern.compile("^test(\\d+)$");
```
- `test1`, `test2`, `test999` ✅ 매칭
- `test01`, `testfile`, `test1.txt` ❌ 매칭 안됨