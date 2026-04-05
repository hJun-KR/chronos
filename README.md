# Chronos

## 프로젝트 개요

기존의 알람 시스템은 특정 시간에만 동작하는 단순한 구조로,
사용자의 다양한 상황과 조건을 반영하기 어렵다는 한계가 있습니다.

Chronos는 이러한 문제를 해결하기 위해,
시간뿐만 아니라 외부 데이터(날씨, 주식 등)를 기반으로 동작하는
조건 중심의 알림 시스템을 제공합니다.

사용자는 자신만의 조건을 설정하여 보다 유연하고 지능적인 알람을 구성할 수 있습니다.

---

## 기술 스택

### Backend

* Framework: Spring Boot (Java 17+)
* Security: Spring Security, JWT
* Data: Spring Data JPA
* Scheduler: Spring Scheduler
* Validation: Jakarta Validation

### Frontend

* Framework: React 19, TypeScript
* Build Tool: Vite
* State Management: TanStack Query
* Styling: Styled Components, Tailwind CSS
* Animation: Framer Motion
* Icons: Lucide React

---

## 실행 방법

### 1. 저장소 클론

```bash id="c1a2d3"
git clone <repository-url>
cd Chronos
```

---

## Backend 실행

### 1. 디렉토리 이동

```bash id="b1a2c3"
cd backend
```

### 2. 환경 변수 설정

`.env` 파일을 생성하거나 `application.yml`을 수정하여 환경 변수를 설정합니다.

```bash id="env123"
JWT_SECRET=your_jwt_secret
DB_URL=your_database_url
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

### 3. 실행

```bash id="run123"
./gradlew bootRun
```

---

## Frontend 실행

### 1. 디렉토리 이동

```bash id="f1a2c3"
cd frontend
```

### 2. 의존성 설치

```bash id="npm123"
npm install
```

### 3. 실행

```bash id="dev123"
npm run dev
```

---

## 주요 기능

### 고급 알람 스케줄링

* Cron 표현식을 활용한 정밀한 알람 설정
* 복합 조건 기반 실행 로직 지원

---

### 조건 기반 트리거

* 날씨 조건 기반 알림 (기온, 강수 등)
* 주식 가격 및 변동률 기반 알림
* 조건 프리셋 저장 및 재사용 기능

---

### 사용자 인증 및 보안

* JWT 기반 인증 및 인가
* 이메일 인증 회원가입
* Refresh Token 기반 세션 유지

---

### 대시보드

* 시각적 조건 빌더 제공
* 실시간 알림 실행 로그 확인
* 직관적인 UI/UX 구성

---

## 프로젝트 구조

```bash id="tree123"
backend/
  # Spring Boot 애플리케이션

frontend/
  # React 기반 프론트엔드

summary.md
  # 프로젝트 문서
```

---

## 주요 특징

* 시간 + 조건 기반의 하이브리드 알람 시스템
* 외부 API 연동을 통한 실시간 데이터 활용
* 확장 가능한 백엔드 구조
* 사용자 중심의 직관적인 인터페이스
