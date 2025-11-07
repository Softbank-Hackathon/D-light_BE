# 커밋 메시지 컨벤션

## 📋 커밋 메시지 구조

```
<type>: <subject>

<body>

<footer>
```

## 🏷️ 타입 (Type)

### 필수 타입

- `feat`: 새로운 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 코드 포맷팅, 세미콜론 누락 등 (기능 변경 없음)
- `refactor`: 코드 리팩토링 (기능 변경 없음)
- `test`: 테스트 추가/수정
- `chore`: 빌드 설정, 패키지 관리 등 (기능 변경 없음)

### 선택 타입

- `perf`: 성능 개선
- `ci`: CI 설정 추가/수정
- `build`: 빌드 시스템 또는 외부 종속성 변경
- `revert`: 이전 커밋 되돌리기

## 📝 제목 (Subject)

- 50자 이내로 작성
- 첫 글자는 소문자로 시작
- 마침표(.)로 끝내지 않음
- 명령형으로 작성 (예: "추가" ❌, "추가한다" ✅)
- 현재 시제 사용

### 좋은 예시 ✅

```
feat: 사용자 인증 기능 추가
fix: 배포 API 검증 로직 수정
docs: README 업데이트
refactor: 서비스 레이어 구조 개선
```

### 나쁜 예시 ❌

```
feat: 사용자 인증 기능을 추가했습니다. (마침표, 과거시제)
FEAT: 사용자 인증 기능 추가 (대문자)
feat: 사용자 인증 기능 (너무 간단)
```

## 📄 본문 (Body)

- 선택사항이지만, 복잡한 변경사항의 경우 작성 권장
- 72자마다 줄바꿈
- 무엇을, 왜 변경했는지 설명
- 어떻게 변경했는지는 코드로 설명 가능하면 생략 가능
- 이전 동작과 현재 동작의 차이점 설명

### 예시

```
feat: OAuth2 GitHub 로그인 구현

- CustomOAuth2UserService를 통해 사용자 정보 처리
- SecurityConfig에서 OAuth2 로그인 경로 설정
- GitHub 사용자 정보를 User 엔티티에 저장

기존에는 사용자 인증이 없었으나, 이제 GitHub OAuth2를
통해 로그인할 수 있게 되었습니다.

Closes #123
```

## 🦶 푸터 (Footer)

### 이슈 참조

- `Closes #이슈번호`: 이슈를 닫음
- `Fixes #이슈번호`: 버그 이슈를 수정함
- `Resolves #이슈번호`: 이슈를 해결함
- `Refs #이슈번호`: 이슈를 참조함 (닫지 않음)

### Breaking Changes

- API 변경 등 호환성을 깨는 변경사항이 있는 경우
- `BREAKING CHANGE:` 접두사 사용

### 예시

```
feat: API 응답 형식 변경

BREAKING CHANGE: CustomApiResponse의 구조가 변경되었습니다.
이전: { data: {...} }
이후: { success: boolean, data: {...}, message: string }

Migration guide: https://github.com/...

Closes #456
```

## 📚 전체 예시

### 간단한 커밋

```
fix: 배포 요청 검증 에러 수정
```

### 복잡한 커밋

```
feat: AWS EC2 배포 기능 구현

- Ec2DeployService에서 EC2 인스턴스 생성 및 설정
- AmiUbuntuService를 통해 Ubuntu AMI 조회
- 배포 스크립트를 통한 자동 배포 프로세스 구현

사용자가 GitHub 저장소를 연결하면 자동으로 EC2 인스턴스에
배포할 수 있도록 구현했습니다.

Closes #123
Related to #456
```

### Breaking Change 포함

```
refactor: DTO 클래스 구조 변경

BREAKING CHANGE: DeploymentRequestDto의 필드명이 변경되었습니다.
- githubRepositoryUrl → repositoryUrl
- projectType → type
- frameworkType → framework

마이그레이션:
- 기존 API를 사용하는 클라이언트는 새로운 필드명으로 업데이트 필요

Closes #789
```

## 🔗 참고 자료

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Angular Commit Message Guidelines](https://github.com/angular/angular/blob/main/CONTRIBUTING.md#commit)

