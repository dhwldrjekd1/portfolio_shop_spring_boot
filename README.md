# Gentle Monster Inspired - 쇼핑몰 포트폴리오 (Backend)

> 젠틀몬스터를 디자인 레퍼런스로 참고하여, 전체 기능과 아키텍처는 직접 설계 및 구현한 풀스택 쇼핑몰 포트폴리오입니다.
>
> **Backend Repository:** [portfolio_shop_spring_boot](https://github.com/dhwldrjekd1/portfolio_shop_spring_boot)
>
> **마지막 업데이트:** 2026-07-20

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
POST /api/order                 주문 생성 (로그인 필요, 세션의 로그인 아이디로만 생성, 서버가 실가격 재계산 후 금액 검증)
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
  .csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
  )
  .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
  .authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/member/**").permitAll()
    .requestMatchers("/api/item/**").permitAll()
    .requestMatchers("/api/review/**").permitAll()
    .requestMatchers("/api/cart/**").permitAll()
    .requestMatchers("/api/wishlist/**").permitAll()
    .requestMatchers("/api/order/**").permitAll()
    .requestMatchers("/api/community/**").permitAll()
    // ... 각 API 허용
    .anyRequest().authenticated()
  )
  .formLogin(AbstractHttpConfigurer::disable)
  .httpBasic(AbstractHttpConfigurer::disable);
```

`permitAll()`은 "Spring Security 필터에서 막지 않는다"는 의미일 뿐, 실제 로그인/관리자 여부는 각 컨트롤러가 `SessionAuth` 헬퍼로 직접 검증합니다. 로그인 성공 시 `HttpSession`에 `loginId`/`role`을 저장하고, 관리자 전용 API는 `role`을, 본인 소유 API는 세션의 `loginId`와 리소스 소유자를 비교합니다. 세션 쿠키는 `HttpOnly` + `SameSite=Lax`로 설정되어 있습니다.

로그인 API(`/api/member/login`)에는 `LoginAttemptService`로 brute-force 방어가 적용되어 있습니다 — 계정(loginId)당 5회 연속 실패 시 5분간 잠금(429 응답), 로그인 성공 시 카운트 초기화, 계정별로 독립 추적됩니다.

컨트롤러 예외 처리는 `ApiError.badRequest(e)`를 통해 응답합니다. DB/JPA 계층 예외(`DataAccessException`)는 서버 로그에만 상세를 남기고 클라이언트에는 일반화된 메시지를 반환해 SQL/테이블 구조 등 내부 정보가 노출되지 않도록 합니다. 서비스에서 직접 던지는 업무 예외 메시지(예: 유효성 검증 실패 안내)는 그대로 노출됩니다.

비밀번호는 `PasswordPolicy`로 서버측 검증됩니다 — 8자 이상 + 영문 소문자·숫자·특수문자 각 최소 1개 포함(회원가입, 비밀번호 변경 모두 적용).

CSRF는 Spring Security 내장 기능(쿠키 기반 토큰, `XSRF-TOKEN`)으로 방어합니다. 프론트엔드는 `csrf.js`가 `fetch`를 감싸 상태변경 요청(POST/PUT/DELETE)마다 쿠키의 토큰을 `X-XSRF-TOKEN` 헤더로 자동 첨부합니다.

주문 금액은 결제수단과 무관하게 `BaseOrderService.validateAmount()`가 서버에서 재계산해 검증합니다. 상품 가격은 DB(`items`)에 있으면 DB 값을, 없으면 정적 시드 카탈로그(`products.json`, `StaticProductCatalog`)에서 조회하며, 클라이언트가 보낸 `amount`가 실제 가격(할인 반영) 합계 + 배송비와 다르면 주문이 거부됩니다.

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

# systemd 서비스로 운영 시 - 유닛 파일에 평문으로 적지 않고, 별도 환경변수 파일을 root 전용
# 권한(600)으로 만들어 EnvironmentFile로 불러온다 (유닛 파일은 systemctl show로 누구나 조회 가능하므로
# Environment=DB_PASSWORD=... 처럼 직접 적으면 비밀번호가 그대로 노출된다)
# /etc/default/gm-backend (600, root:root)
DB_PASSWORD=본인_postgres_비밀번호

# gm-backend.service
EnvironmentFile=/etc/default/gm-backend
```

토스페이먼츠 키, 메일 발송(SMTP) 정보도 같은 파일에서 채워야 결제/비밀번호 찾기 기능이 정상 동작합니다.

---

## 로컬 개발 환경 설정 (처음 셋업 시)

`application.properties.example` 복사 + 환경변수 주입 외에, 아래 항목들은 코드/문서에 자동화되어 있지 않아 직접 준비해야 합니다.

1. **DB 자체를 미리 생성해야 함** — `spring.jpa.hibernate.ddl-auto=update`는 DB *안의 테이블*만 자동 생성하며, DB 자체는 만들어주지 않습니다. 로컬 Postgres에 먼저 생성해야 합니다.
   ```bash
   createdb -U postgres shop
   ```
2. **업로드 경로를 로컬 경로로 변경** — `file.upload-dir`이 예시 파일에 이 서버 전용 절대경로(`/var/www/gm_backend/uploads`)로 들어있습니다. 로컬 환경에 맞는 경로로 바꾸고, 해당 디렉토리를 미리 만들어둬야 상품 이미지 업로드가 정상 동작합니다.
   ```properties
   file.upload-dir=/원하는/로컬/경로
   ```
   ```bash
   mkdir -p /원하는/로컬/경로
   ```
3. **프론트엔드를 별도 dev 서버로 띄우면 API 연동이 안 됨** — `vite.config.js`에 백엔드로 가는 dev 프록시가 설정되어 있지 않아, `npm run dev`로 프론트만 띄우면 `/api/**` 요청이 그대로 실패합니다. 프론트 변경사항을 API와 함께 확인하려면 `npm run build` 후 결과물을 백엔드의 `src/main/resources/static/web03`에 복사하고 백엔드를 재시작해서 확인해야 합니다 (실제 배포와 동일한 방식).

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

## 트러블슈팅

- **재고 검증/차감 로직 부재** (2026-07-19) — 재고 차감이 "배송완료" 처리 시점에만, 그것도 조회 후 다시 쓰는(read-then-write) 방식으로 이뤄져 있어 주문 생성 시점엔 재고 확인 자체가 없었고 동시 주문 시 초과 판매가 가능했던 문제. 차감 시점을 주문 생성 시점으로 옮기고, `stock >= 수량` 조건을 만족할 때만 갱신되는 원자적 UPDATE 쿼리(`ItemRepository.decreaseStockIfAvailable`)로 교체. 관련 서비스 메서드에 `@Transactional`을 적용해 중간 실패 시 롤백되도록 함.
- **주문 취소 상태 미검증** (2026-07-19) — 이미 배송완료된 주문도 취소 API로 취소할 수 있던 문제. 취소는 "주문접수"/"배송중" 상태에서만 허용하도록 컨트롤러에 검증 추가.
- **취소 동시 요청 시 재고 이중 복구** (2026-07-19) — 같은 주문에 취소 요청이 동시에 들어오면(중복 클릭 등) 재고가 실제 차감량보다 많이 복구될 수 있는 레이스 컨디션이 있던 문제. 주문 상태 변경 시 `SELECT ... FOR UPDATE` 기반 행 잠금 조회(`findByIdForUpdate`)로 동시 요청을 직렬화.
- **주문 삭제 시 재고 미복구** (2026-07-19) — 관리자가 주문을 취소가 아닌 삭제로 처리하면, 주문 생성 시 차감된 재고가 복구되지 않고 영구히 사라지던 문제. 삭제 전 취소되지 않은 주문이면 재고를 먼저 복구하도록 수정.
- **주문 수량 음수 입력 검증 누락** (2026-07-19) — 주문 수량에 음수 값을 넣으면 재고 차감 쿼리가 오히려 재고를 늘리는 방향으로 동작할 수 있던 문제. 주문 금액 검증 단계에서 수량이 1 이상인지 확인하도록 검증 추가.
- **토스 결제와 주문 생성이 서로 연결되지 않음** (2026-07-19) — `/api/payment/confirm`으로 결제 승인을 받아도 그 결과가 저장되지 않아, `/api/order`를 결제 없이 직접 호출해도 주문이 생성되던 문제. 또한 결제 1건으로 여러 주문을 만드는 것도 막을 방법이 없었음. 승인된 결제를 `toss_payments` 테이블에 기록해두고, 주문 생성 시(`payment="toss"`) 해당 결제가 존재하는지·아직 사용되지 않았는지·금액이 일치하는지 검증한 뒤에만 주문을 생성하고 결제를 사용 처리하도록 수정.
- **일부 API에 전역 예외 안전망 부재** (2026-07-19) — 대부분의 컨트롤러는 `try/catch`로 `ApiError.badRequest(e)`를 거치지만, 일부 GET 엔드포인트는 이마저 없어 예외 발생 시 스택트레이스나 내부 메시지가 그대로 노출될 여지가 있던 문제. `@RestControllerAdvice` 기반 전역 예외 처리기를 추가해, 컨트롤러에서 놓친 예외도 동일한 형식·동일한 정보 노출 수준으로 응답하도록 보완.
- **토스 결제수단 검증이 대소문자로 우회 가능** (2026-07-19) — 결제 승인 대조 로직이 `payment` 값을 `"toss"`와 정확히 일치하는 경우에만 적용해서, `/api/order`를 직접 호출하며 `payment`를 `"Toss"`처럼 다르게 보내면 검증이 통째로 스킵되던 문제. 결제 수단을 화이트리스트(`toss/kakao/naver/bank`)로 검증하고 trim+소문자로 정규화한 뒤 비교·저장하도록 수정.
- **결제 재사용 방지 로직에 동시성 레이스** (2026-07-19) — 결제 승인 기록의 사용 여부를 "조회 → 확인 → 변경 → 저장" 방식으로 처리해서, 같은 결제로 동시에 주문 요청 2건이 들어오면 둘 다 통과해 결제 1건으로 주문이 2개 생성될 수 있던 문제. 재고 차감과 동일하게 `used=false`일 때만 원자적으로 전환되는 조건부 UPDATE로 교체.
- **임시 비밀번호가 자체 비밀번호 정책을 만족하지 못할 수 있음** (2026-07-19) — 비밀번호 찾기로 발급되는 임시 비밀번호가 `UUID` 앞 8자리(16진수)라 특수문자가 보장되지 않던 문제. 영문 소문자/숫자/특수문자를 각각 최소 1개 포함하도록 보장하는 생성 로직으로 교체.
- **회원 등급 계산에 죽은 분기** (2026-07-19) — `updateGradeByAmount`에서 서로 다른 조건 2개가 동일하게 "브론즈"를 반환하던 중복 분기 정리 (동작 변화 없음).
- **주문 상품별 단가 미저장으로 매출 통계 왜곡** (2026-07-20) — `order_items`에 주문 시점 단가가 저장되지 않아, 관리자 판매 통계(상품별 매출)를 프론트에서 "주문 총액 ÷ 주문 내 상품 종류 수"로 근사 계산하고 있었음. 이 방식은 한 주문에 상품이 여러 종류이고 수량이 2개 이상인 항목이 섞이면 상품별 매출 합계가 실제 주문 총액을 초과하는 오류가 있었음. `OrderItem`에 `price`(할인 반영 단가) 컬럼을 추가하고 주문 생성 시점에 고정 저장하도록 수정 (`BaseOrderService.getUnitPrice()`로 `validateAmount`와 동일한 계산 로직 공유). 컬럼 추가 이전에 생성된 과거 주문은 `price`가 없어 프론트가 현재 상품가로 대체 계산.

---

## 알려진 구조적 한계

- **Spring Security `authorizeHttpRequests`가 `/api/**` 전체를 `permitAll()`로 설정** — 이 프로젝트는 로그인/권한 검증을 Spring Security의 인증 메커니즘이 아니라 `HttpSession` 기반 커스텀 방식(`SessionAuth`)으로 직접 처리합니다. 따라서 Spring Security 자체는 로그인 여부를 알지 못하고, 실제 인가는 각 컨트롤러가 `SessionAuth.isAdmin()` / `isSelfOrAdmin()`을 호출해 개별적으로 보장합니다. `authorizeHttpRequests`를 `.authenticated()` 등으로 강화하면 Spring Security의 인증 상태가 항상 비어있어 로그인 여부와 무관하게 전체 API가 차단되므로, 안전하게 고치려면 Spring Security의 `Authentication`을 커스텀 세션과 연동하는 별도의 인증 구조 개편이 필요합니다. 현재는 컨트롤러별 수동 검증에 의존하는 상태를 의도적으로 유지하고 있습니다.
- **주문 금액 검증에 `double` 연산 사용** (`BaseOrderService.validateAmount`) — 프론트(`getDiscountedPrice`)가 할인 없는 상품가는 반올림하지 않고 그대로 합산하기 때문에, 백엔드도 동일하게 `double`로 맞춰야 두 계산이 항상 일치합니다. `long`/정수 연산으로 바꾸려면 할인 없는 상품가에도 반올림이 들어가게 되어 프론트와 계산이 어긋나는 경우(상품가가 정수가 아닐 때)가 생길 수 있어, 프론트 쪽도 함께 바꾸지 않는 한 현재의 `double` 방식을 그대로 유지하고 있습니다.

---

## 연관 레포지토리

| 구분 | 링크 |
|------|------|
| Frontend | [portfolio_shop_frontend](https://github.com/dhwldrjekd1/portfolio_shop_frontend) |
