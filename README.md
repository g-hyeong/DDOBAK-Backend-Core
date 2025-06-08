# 🚀 DDOBAK Server

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)

> **DDOBAK 서비스 백엔드 API 서버**

## 🎯 프로젝트 소개

Spring Boot로 만든 REST API 서버입니다. 

### 🔧 기술 스택
- **Java 17** + **Spring Boot 3.5.0**

## 📁 프로젝트 구조

```
...
```

## 🚀 시작하기

### 1. 프로젝트 실행
```bash
# 프로젝트 클론
git clone [repository-url]
cd DDOBAK-Server/server

# 로컬 실행
./gradlew bootRun
```

### 2. API 문서 확인
- http://localhost:8080/swagger-ui.html

## 👥 팀 작업 방식

### 🌳 브랜치 사용법
```bash
main       # 배포용
  ├── dev  # 개발 메인
  ├── feat/기능명  # 새 기능 개발
  └── fix/버그명   # 버그 수정
  └── ...
```

### 📝 브랜치 이름 예시
```bash
feat/user-login     # 로그인 기능
feat/file-upload    # 파일 업로드
fix/login-bug       # 로그인 버그 수정
```

### 💬 커밋 메시지
```bash
feat: 로그인 기능 추가
fix: 회원가입 오류 수정
docs: README 업데이트
```

## 🔄 개발 흐름

1. **이슈 확인** → GitHub Issues 에서 할 일 확인
2. **브랜치 생성** → `feat/기능명` 으로 브랜치 만들기
3. **코딩** → 기능 개발
4. **테스트** → 로컬에서 잘 돌아가는지 확인
5. **PR 생성** → Pull Request 올리기
6. **코드 리뷰** → 팀원들이 코드 확인
7. **머지** → dev 브랜치에 합치기

## 📚 개발 규칙

### 🎯 Cursor Rules
프로젝트의 코딩 규칙은 `.cursor/rules/` 폴더에 있습니다:

### 📖 문서 구조
```
docs/
├── api-development-guide.md  # API 개발 가이드 (Response/Exception 처리)
└── (추후 추가 예정...)
```

## 📚 개발 가이드

### 🔧 API 개발 시 참고사항
- **Response/Exception 처리**: [API 개발 가이드](docs/api-development-guide.md) 참고
- **Common 모듈 활용**: `ApiResponse<T>`, 예외 클래스들 필수 사용
- **에러 코드 체계**: 2xxx(성공), 4xxx(클라이언트), 5xxx(서버) 분류 준수
