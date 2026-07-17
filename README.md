# Gentle Monster Inspired - 쇼핑몰 포트폴리오 (Backand)

> 젠틀몬스터를 디자인 레퍼런스로 참고하여, 전체 기능과 아키텍처는 직접 설계 및 구현한 풀스택 쇼핑몰 포트폴리오입니다.
>
> **Backand Repository:** [portfolio_shop_spring_boot](https://github.com/dhwldrjekd1/portfolio_shop_spring_boot)

---

## 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | Gentle Monster Inspired 쇼핑몰 |
| 개발 기간 | 2026.03 |
| 개발자    | 최동윤 |
| 개발 인원 | 1인 (풀스택) |
| 배포 환경 | Docker (Ubuntu 컨테이너) |
| 접속 URL | `http://localhost:8086/web03/` |

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Framework | Spring Boot 3.2.0 (Java 21) |
| 보안 | Spring Security (BCrypt, SecurityFilterChain) |
| ORM | Spring Data JPA + Hibernate |
| DB | PostgreSQL 14 |
| 문서화 | Swagger (SpringDoc OpenAPI 3) |
| 기타 | Lombok, Docker |

---

## 프로젝트 구조

```
src/main/java/com/example/demo/
├── member/           # 회원 도메인
│   ├── entity/Member.java
│   ├── service/MemberService.java
│   ├── service/BaseMemberService.java
│   └── controller/MemberController.java
├── item/             # 상품 도메인
├── cart/             # 장바구니 도메인
├── wishlist/         # 위시리스트 도메인
├── order/            # 주문 도메인
├── review/           # 리뷰 도메인
├── board/            # 커뮤니티 도메인
├── notice/           # 공지사항 도메인
├── qna/              # QnA 도메인
├── inquiry/          # 고객문의 도메인
└── config/
    ├── SecurityConfig.java   # Spring Security 설정
    ├── WebConfig.java        # 정적 리소스 설정
    ├── FileConfig.java       # 파일 업로드 설정
    ├── JpaConfig.java        # 시간 자동 처리
    └── SpaController.java    # SPA 라우팅 처리
```

---

## 구현 기능

> 아래 (관리자) / (본인) 표시는 세션 기반 서버측 권한 검증(`SessionAuth`)이 적용되어 있다는 의미입니다. 로그인 필요 API는 세션에 저장된 `loginId`로 소유자를 판별합니다.

### 회원 API
```
POST /api/member/register     회원가입
POST /api/member/login        로그인 (BCrypt 검증, 성공 시 서버 세션 발급)
POST /api/member/logout       로그아웃 (서버 세션 무효화)
GET  /api/member/info/{id}    회원정보 조회 (본인/관리자)
PUT  /api/member/update/{id}  회원정보 수정 (본인/관리자)
DELETE /api/member/delete/{id} 회원탈퇴 (본인/관리자)
POST /api/member/find-pw      비밀번호 찾기 (임시 비밀번호를 이메일로 발송, 응답에는 미노출)
GET  /api/member/list         회원 목록 (관리자)
PUT  /api/member/grade/{id}   등급 변경 (관리자)
PUT  /api/member/role/{id}    권한 변경 (관리자)
```

### 상품 API
```
GET    /api/item               전체 상품 조회
GET    /api/item/{id}          단일 상품 조회
POST   /api/item               상품 등록 (관리자, multipart, 이미지 확장자/MIME 검증)
PUT    /api/item/{id}          상품 수정 (관리자, multipart, 이미지 확장자/MIME 검증)
DELETE /api/item/{id}          상품 삭제 (관리자)
PUT    /api/item/{id}/stock    재고 수정 (관리자)
PUT    /api/item/{id}/discount 할인율 수정 (관리자)
PUT    /api/item/{id}/details  세부정보 수정 (관리자, JSON)
```

### 장바구니 API
```
GET    /api/cart/{loginId}       장바구니 조회 (본인/관리자)
POST   /api/cart                 장바구니 추가 (로그인 필요)
PUT    /api/cart/{id}            수량 변경 (본인/관리자)
DELETE /api/cart/{id}            개별 삭제 (본인/관리자)
DELETE /api/cart/clear/{loginId} 전체 삭제 (본인/관리자)
```

### 위시리스트 API
```
GET    /api/wishlist/{loginId}          위시리스트 조회 (본인/관리자)
POST   /api/wishlist                    위시리스트 추가 (로그인 필요)
DELETE /api/wishlist/{loginId}/{itemId} 위시리스트 삭제 (본인/관리자)
```

### 주문 API
```
GET  /api/order/{loginId}       회원 주문 목록 (본인/관리자)
POST /api/order                 주문 생성 (로그인 필요, 세션의 로그인 아이디로만 생성)
GET  /api/order/all             전체 주문 (관리자)
PUT  /api/order/{id}/status     상태 변경 (관리자)
PUT  /api/order/{id}/cancel     주문 취소 (본인/관리자)
DELETE /api/order/{id}          주문 삭제 (관리자)
```

### 리뷰 API
```
GET  /api/review/item/{itemId}                      상품별 리뷰
GET  /api/review/member/{loginId}                   회원별 리뷰
GET  /api/review/all                                전체 리뷰 (관리자)
POST /api/review                                    리뷰 등록 (로그인 필요)
PUT  /api/review/{id}                               리뷰 수정 (작성자 본인/관리자)
DELETE /api/review/{id}                              리뷰 삭제 (작성자 본인/관리자)
GET  /api/review/check/purchased/{loginId}/{itemId} 구매 확인
GET  /api/review/check/reviewed/{loginId}/{itemId}  리뷰 작성 확인
```

### 고객문의 / 커뮤니티 API
```
GET  /api/inquiry/my/{loginId}   내 문의 목록 (본인/관리자)
GET  /api/inquiry/all            전체 문의 목록 (관리자)
POST /api/inquiry/{id}/reply     답글 등록 (관리자)
PUT  /api/inquiry/{id}           문의 수정 (작성자 본인/관리자)
DELETE /api/inquiry/{id}         문의 삭제 (작성자 본인/관리자)

POST   /api/notice, PUT /api/notice/{id}, DELETE /api/notice/{id}   공지 등록/수정/삭제 (관리자)
POST   /api/qna, PUT /api/qna/{id}, DELETE /api/qna/{id}            QnA 등록/수정/삭제 (관리자)

PUT    /api/community/{id}              게시글 수정 (작성자 본인/관리자)
DELETE /api/community/{id}              게시글 삭제 (작성자 본인/관리자)
DELETE /api/community/comments/{id}     댓글 삭제 (작성자 본인/관리자)
```

---

## DB 테이블

```sql
-- 12개 테이블
items        -- 상품 (badge, category, details_json 포함)
members      -- 회원 (grade, role 포함)
carts        -- 장바구니
wishlists    -- 위시리스트 (loginId + itemId 유니크 제약)
orders       -- 주문
order_items  -- 주문 상품
notices      -- 공지사항
qnas         -- QnA
inquiries    -- 고객문의
boards       -- 커뮤니티 게시판
comments     -- 댓글
reviews      -- 리뷰
```

---

## Spring Security 설정

```java
http
  .csrf(AbstractHttpConfigurer::disable)        // REST API 방식
  .authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/member/**").permitAll()
    .requestMatchers("/api/item/**").permitAll()
    .requestMatchers("/api/review/**").permitAll()
    .requestMatchers("/api/cart/**").permitAll()
    .requestMatchers("/api/wishlist/**").permitAll()
    .requestMatchers("/api/order/**").permitAll()
    // ... 각 API 허용
    .anyRequest().authenticated()
  )
  .formLogin(AbstractHttpConfigurer::disable)
  .httpBasic(AbstractHttpConfigurer::disable);
```

`permitAll()`은 "Spring Security 필터에서 막지 않는다"는 의미일 뿐, 실제 로그인/관리자 여부는 각 컨트롤러가 `SessionAuth` 헬퍼로 직접 검증합니다. 로그인 성공 시 `HttpSession`에 `loginId`/`role`을 저장하고, 관리자 전용 API는 `role`을, 본인 소유 API는 세션의 `loginId`와 리소스 소유자를 비교합니다. 세션 쿠키는 `HttpOnly` + `SameSite=Lax`로 설정되어 있습니다.

로그인 API(`/api/member/login`)에는 `LoginAttemptService`로 brute-force 방어가 적용되어 있습니다 — 계정(loginId)당 5회 연속 실패 시 5분간 잠금(429 응답), 로그인 성공 시 카운트 초기화, 계정별로 독립 추적됩니다.

컨트롤러 예외 처리는 `ApiError.badRequest(e)`를 통해 응답합니다. DB/JPA 계층 예외(`DataAccessException`)는 서버 로그에만 상세를 남기고 클라이언트에는 일반화된 메시지를 반환해 SQL/테이블 구조 등 내부 정보가 노출되지 않도록 합니다. 서비스에서 직접 던지는 업무 예외 메시지(예: 유효성 검증 실패 안내)는 그대로 노출됩니다.

```java
// 관리자 전용 API 예시
if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();

// 본인 소유 리소스 API 예시
if (!SessionAuth.isSelfOrAdmin(request, resourceOwnerLoginId)) return SessionAuth.forbidden();
```

---

## 환경 설정 (application.properties)

`src/main/resources/application.properties`는 DB 비밀번호 등 민감정보가 포함되어 있어 `.gitignore` 처리되어 있고, git에는 커밋되지 않습니다. 대신 `application.properties.example`을 참고해서 로컬에 직접 만들어야 합니다.

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

DB 비밀번호는 파일에 직접 적지 않고 환경변수 `DB_PASSWORD`로 주입합니다.

```bash
# 로컬 실행 시
export DB_PASSWORD=본인_postgres_비밀번호
./gradlew bootRun

# systemd 서비스로 운영 시 (gm-backend.service 예시)
Environment=DB_PASSWORD=본인_postgres_비밀번호
```

토스페이먼츠 키, 메일 발송(SMTP) 정보도 같은 파일에서 채워야 결제/비밀번호 찾기 기능이 정상 동작합니다.

---

## 배포 방법

```bash
# 1. JAR 빌드
gradlew.bat clean build -x test

# 2. Docker에 복사
docker cp build/libs/shop-0.0.1-SNAPSHOT.jar ubuntu01:/root/

# 3. 서버 실행
docker exec -it ubuntu01 bash
pkill -f shop && sleep 2
java -jar /root/shop-0.0.1-SNAPSHOT.jar &
```

---

## 연관 레포지토리

| 구분 | 링크 |
|------|------|
| Frontend | [portfolio_shop_frontend](https://github.com/dhwldrjekd1/portfolio_shop_frontend) |
