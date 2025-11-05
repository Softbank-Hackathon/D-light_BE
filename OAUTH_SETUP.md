# GitHub OAuth2 로그인 설정 가이드

## 📋 개요

이 프로젝트는 GitHub OAuth2를 사용하여 사용자 인증을 처리합니다.

## 🛠️ 로컬 개발 환경 설정

### 1. MySQL 데이터베이스 준비

```bash
mysql -u root -p
CREATE DATABASE Dlight CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

### 2. application.yml 설정

1. `src/main/resources/application.yml.example` 파일을 복사하여 `application.yml` 생성
   ```bash
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   ```

2. `application.yml` 파일에서 다음 항목 수정:
   - `spring.datasource.password`: 본인의 MySQL 비밀번호

### 3. GitHub OAuth App 생성

1. GitHub 설정으로 이동: https://github.com/settings/developers
2. "New OAuth App" 클릭
3. 다음 정보 입력:
   - **Application name**: `Dlight-Local` (또는 원하는 이름)
   - **Homepage URL**: `http://localhost:8080`
   - **Authorization callback URL**: `http://localhost:8080/login/oauth2/code/github`

     ⚠️ **중요**: 이 URL은 정확히 일치해야 합니다!

4. "Register application" 클릭
5. **Client ID**와 **Client Secret** 복사

### 4. 환경변수 설정

#### IntelliJ IDEA 사용 시:

1. **Run > Edit Configurations...**
2. **MelonApplication** 선택 (없으면 생성)
3. **Environment variables** 입력:
   ```
   GITHUB_CLIENT_ID=your_client_id_here;GITHUB_CLIENT_SECRET=your_client_secret_here
   ```

#### 터미널에서 실행 시:

```bash
export GITHUB_CLIENT_ID=your_client_id_here
export GITHUB_CLIENT_SECRET=your_client_secret_here
./gradlew bootRun
```

## 🚀 실행 및 테스트

### 1. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 IntelliJ에서 `MelonApplication` 실행

### 2. OAuth2 로그인 테스트

1. 브라우저에서 접속:
   ```
   http://localhost:8080/oauth2/authorization/github
   ```

2. GitHub 로그인 페이지로 리다이렉트됨
3. GitHub 계정으로 로그인 및 권한 승인
4. 로그인 성공 시 `users` 테이블에 사용자 정보 저장됨

### 3. 데이터베이스 확인

```bash
mysql -u root -p
USE Dlight;
SELECT * FROM users;
```

## 📦 주요 변경 사항

### 1. 의존성 추가 (build.gradle)
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
```

### 2. 새로 추가된 파일

```
src/main/java/com/hackathon/melon/
├── domain/
│   ├── auth/
│   │   └── service/
│   │       └── CustomOAuth2UserService.java    # OAuth2 사용자 정보 처리
│   └── user/
│       ├── entity/
│       │   └── User.java                        # 사용자 엔티티
│       └── repository/
│           └── UserRepository.java              # 사용자 레포지토리
└── global/
    └── config/
        └── swagger/
            └── SecurityConfig.java              # Spring Security 설정
```

### 3. 주요 설정

#### SecurityConfig.java
- OAuth2 로그인 활성화
- 인증 없이 접근 가능한 경로: `/`, `/health`, `/error`
- 나머지 경로는 인증 필요

#### CustomOAuth2UserService.java
- GitHub에서 받은 사용자 정보를 처리
- 신규 사용자는 DB에 저장
- 기존 사용자는 정보 업데이트

#### User 엔티티
- GitHub ID, 로그인명, 프로필 URL, 이메일 저장
- `BaseEntity` 상속으로 `createdAt`, `updatedAt` 자동 관리

## 🔒 보안 주의사항

1. **절대 커밋하지 말 것:**
   - `application.yml` (실제 DB 비밀번호 포함)
   - GitHub Client ID/Secret

2. **이미 .gitignore에 추가됨:**
   ```
   src/main/resources/application.yml
   src/main/resources/application-*.yml
   ```

3. **팀원과 공유할 파일:**
   - `application.yml.example` (템플릿)
   - 이 문서 (`OAUTH_SETUP.md`)

## 🐛 문제 해결

### "Invalid credentials" 에러

**원인**: GitHub OAuth App 설정이 잘못되었거나 환경변수가 설정되지 않음

**해결**:
1. GitHub OAuth App의 Callback URL 확인
2. 환경변수 `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET` 확인
3. IntelliJ Run Configuration에서 환경변수 재설정

### 로그인 후 에러 발생

**원인**: MySQL 연결 실패 또는 테이블 생성 실패

**해결**:
1. MySQL이 실행 중인지 확인
2. `Dlight` 데이터베이스가 생성되어 있는지 확인
3. `application.yml`의 DB 비밀번호 확인

### "redirect_uri_mismatch" 에러

**원인**: GitHub OAuth App의 Callback URL이 일치하지 않음

**해결**:
- GitHub OAuth App 설정에서 Authorization callback URL을 정확히 입력:
  ```
  http://localhost:8080/login/oauth2/code/github
  ```

## 📚 OAuth2 로그인 흐름

```
1. 사용자 → /oauth2/authorization/github 접속
2. Spring Security → GitHub 로그인 페이지로 리다이렉트
3. 사용자 → GitHub에서 로그인 및 권한 승인
4. GitHub → 인가 코드를 callback URL로 전달
5. Spring Security → 인가 코드로 액세스 토큰 요청
6. Spring Security → 액세스 토큰으로 사용자 정보 조회
7. CustomOAuth2UserService → 사용자 정보 DB 저장/업데이트
8. 로그인 완료
```

## 📞 문의

문제가 발생하면 팀 채널에 문의하세요!
