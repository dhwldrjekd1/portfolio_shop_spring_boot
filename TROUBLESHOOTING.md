#  Gentle Monster — Inspired Commerce Platform

> 젠틀몬스터를 디자인 레퍼런스로 참고하여 직접 설계·구현한 풀스택 프로젝트  
> A full-stack platform inspired by Gentle Monster, designed and built from scratch.

---

## Project Overview

| 항목 | 내용 |
|------|------|
| **프로젝트명** | Gentle Monster Inspired Commerce |
| **개발 기간** | 2026.03 (1인 개발) |
| **개발 형태** | 풀스택 1인 프로젝트 |
| **개발자**    | 최동윤 |
| **목표** | 실제 커머스 비즈니스 로직 설계 + Spring Security 기반 보안 구조 구현 |

<br>

---

## Tech Stack

### Frontend
| 기술 | 선택 이유 |
|------|----------|
| **Vue.js 3** (Composition API) | `script setup` 기반 컴포넌트 설계로 관심사 분리 및 재사용성 확보 |
| **Pinia** | 전역 상태 중앙화, 장바구니·로그인 상태 SPA 전반에서 일관 관리 |
| **Vue Router 4** | SPA 라우팅 및 Navigation Guard를 통한 비로그인·비관리자 접근 차단 |
| **Vite** | ESM 기반 빠른 빌드 환경으로 개발 생산성 향상 |

### Backend
| 기술 | 선택 이유 |
|------|----------|
| **Spring Boot 3.2 / Java 21** | 안정적인 RESTful API 서버 구축 |
| **Spring Security** | BCrypt 암호화 + SecurityFilterChain 기반 보안 레이어 분리 |
| **Spring Data JPA (Hibernate)** | 객체-관계 매핑을 통한 도메인 중심 개발 및 데이터 정합성 관리 |
| **PostgreSQL** | 트랜잭션 보장이 필요한 커머스 도메인에 적합한 RDBMS |

### Infrastructure
| 기술 | 역할 |
|------|------|
| **Docker** | 앱 / DB 컨테이너화로 환경 일관성 보장 |
| **Docker Hub** | 이미지 배포 및 다른 환경에서의 재현 가능한 실행 환경 제공 |
| **Ubuntu (Linux)** | JAR 실행 및 프로세스 관리 |

<br>

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│                   Client (Browser)              │
│         Vue 3 + Pinia + Vue Router 4            │
└──────────────────────┬──────────────────────────┘
                       │ REST API (HTTP/JSON)
┌──────────────────────▼──────────────────────────┐
│              Spring Boot 3.2 Server             │
│  ┌──────────────────┐  ┌─────────────────────┐  │
│  │  Security Filter │  │   Business Logic    │  │
│  │  Chain (BCrypt)  │  │  Order/Member/Item  │  │
│  └──────────────────┘  └──────────┬──────────┘  │
│                                   │ JPA/Hibernate│
└───────────────────────────────────┼─────────────┘
                                    │
┌───────────────────────────────────▼─────────────┐
│              PostgreSQL (Docker)                │
│            11 Tables — 커머스 전체 도메인        │
└─────────────────────────────────────────────────┘
```

<br>

---

## Key Business Logic

### 1. 회원 등급 자동화 시스템

배송완료 주문 누적 금액에 따라 등급을 자동 산정하며, 주문 취소 시 재계산으로 정합성을 보장합니다.

```
브론즈 :   0 ~  50만원
실버   :  50 ~ 100만원
골드   : 100 ~ 150만원
플래티넘: 150만원 이상
```

- 배송완료 확정 시점에 등급 반영 → 어뷰징 방지
- 주문 취소 시 금액 차감 후 등급 재계산 → 데이터 정합성 보장
- 관리자가 수동으로 등급 변경 가능

---

### 2. 주문 / 재고 파이프라인

```
[주문 생성] → sessionStorage 스냅샷 저장 → 장바구니 비우기
      ↓
[배송완료 확정] → 재고 차감 → 등급 업데이트
      ↓
[주문 취소] → 재고 복구 → 등급 재계산
```

- 배송완료 시점에만 재고 차감 → 재고 오차 방지
- 취소 시 재고 즉시 복구
- `order_items`에 주문 시점 상품명/색상/사이즈 스냅샷 저장 → 상품 삭제 후에도 주문 내역 유지

---

### 3. 관리자 대시보드 (6개 탭)

| 탭 | 기능 |
|----|------|
| 회원 관리 | 검색, 등급 변경, 권한 변경, 강퇴 |
| 주문 관리 | 검색, 기간 필터, 페이징(10건), 상태 변경, 삭제 |
| 재고 관리 | 상품별 재고/할인율 수정 |
| 판매 관리 | 매출 통계, 상품별 판매량, 기간 필터 |
| 상품 관리 | 등록/수정/삭제, 이미지 업로드(파일/URL), 세부정보 수정 |
| 리뷰 관리 | 전체 리뷰 목록, 수정, 삭제 |

<br>

---

## Engineering & Troubleshooting

### [Case 1] Docker 컨테이너 포트 충돌 (좀비 프로세스)

**문제** : JAR 재배포 시 `Port 8086 was already in use` 오류  
**원인** : 기존 Java 프로세스가 백그라운드에서 계속 실행 중 → 포트 점유 지속  
**해결** : `pkill` + `sleep` 조합으로 기존 프로세스 Graceful Shutdown 후 재시작

```bash
pkill -f shop
sleep 2
java -jar shop-0.0.1-SNAPSHOT.jar &
```

---

### [Case 2] DB NOT NULL 제약 조건 오류 (이미지 없는 상품 등록)

**문제** : 이미지 없이 상품 등록 시 `null value in column "image_path" violates not-null constraint`  
**원인** : `items` 테이블의 `image_path` 컬럼에 NOT NULL 제약 조건  
**해결** : 컬럼 제약 조건 제거 + 엔티티 nullable 처리

```sql
ALTER TABLE items ALTER COLUMN image_path DROP NOT NULL;
```

---

### [Case 3] JPA Entity ID 생성 전략 충돌

**문제** : 신규 상품 등록 시 `duplicate key value violates unique constraint` 발생  
**원인** : 초기 데이터를 수동 삽입하면서 DB 시퀀스 값과 실제 PK 불일치  
**해결** : `SEQUENCE` → `IDENTITY` 전략으로 변경, ID 생성 권한을 DB에 완전히 위임

```java
// Before — 시퀀스 전략: 수동 삽입 데이터와 충돌
@GeneratedValue(strategy = GenerationType.SEQUENCE)

// After — DB AUTO_INCREMENT에 위임, 충돌 원천 차단
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

---

### [Case 4] Spring Security 적용 후 전체 API 401 오류

**문제** : `spring-boot-starter-security` 추가 후 공개 API 포함 모든 요청 401 반환  
**원인** : Security 기본 설정이 모든 요청에 인증 요구  
**해결** : `requestMatchers` 화이트리스트 설정으로 API별 접근 권한 분리

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/member/**").permitAll()
    .requestMatchers("/api/item/**").permitAll()
    .requestMatchers("/api/order/**").permitAll()
    // ...
    .anyRequest().authenticated()
)
```

---

### [Case 5] Spring Security 기본 비밀번호 자동 생성으로 인한 로그인 400 오류

**문제** : Security 적용 후 `Using generated security password` 경고 발생, 로그인 API 400 오류  
**원인** : 기본 `UserDetailsService`가 활성화되어 랜덤 비밀번호를 생성하고 HTTP Basic 인증을 요구  
**해결** : 빈 `InMemoryUserDetailsManager` 등록으로 기본 비밀번호 생성 방지

```java
@Bean
public UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(); // 빈 매니저 등록
}
```

---

### [Case 6] 리뷰 작성 완료 후에도 작성 폼이 계속 표시됨

**문제** : 리뷰를 이미 작성했음에도 상품 상세 페이지에서 리뷰 작성 폼이 계속 표시됨  
**원인** : `canWriteReview` computed 조건에서 `hasReviewed` 체크 누락  
**해결** : 구매 여부와 리뷰 작성 여부를 동시에 검사하는 조건으로 수정

```
// Before
canWriteReview = hasPurchased

// After — 구매했고 아직 리뷰 안 쓴 경우만 작성 가능
canWriteReview = hasPurchased && !hasReviewed
```

---

### [Case 7] products.json 하드코딩 리뷰와 DB 리뷰 혼재

**문제** : 상품 상세 페이지에 DB 리뷰와 정적 파일 리뷰가 동시에 표시됨  
**원인** : Pinia store가 `products.json`의 `reviews` 필드를 그대로 로드하고 있었음  
**해결** : `products.json`에서 `reviews`, `rating`, `reviewCount` 필드 완전 제거, DB API로만 리뷰 조회

```
products.json 정리 → DB API(/api/review/item/{id})로 리뷰 단독 조회
→ 리뷰 중복 표시 해결 + 실시간 평균 별점 정확도 향상
```

---

### [Case 8] 관리자 신규 등록 상품이 상품 목록에 미표시

**문제** : 관리자가 신규 상품을 등록해도 상품 목록 페이지에 표시되지 않음  
**원인** : `store.js`의 `fetchData()`가 `products.json`에 있는 상품만 처리하고, DB에만 있는 신규 상품은 무시  
**해결** : DB 전체 상품과 JSON 상품을 비교하여 DB에만 있는 상품을 별도로 합치는 로직 추가

```
jsonIds = products.json의 ID 목록
dbOnly  = DB 상품 중 jsonIds에 없는 것 → 신규 등록 상품
products = [...jsonProducts, ...dbOnly]
```

---

### [Case 9] 결제 완료 후 주문 상품 이미지 유실

**문제** : 주문완료 페이지에서 주문 상품 이미지/정보가 표시되지 않음  
**원인** : `clearCart()` 실행으로 Pinia 스토어 초기화 후 페이지 이동 → 상품 데이터 접근 불가  
**해결** : `clearCart()` 호출 전에 핵심 데이터를 `sessionStorage`에 캐싱, 주문완료 페이지에서 복원 후 삭제

```
결제 완료 직전
  → sessionStorage에 주문 상품 정보 저장   ← 핵심 포인트
  → clearCart() 실행
  → 주문완료 페이지 이동
  → sessionStorage에서 데이터 복원하여 표시
  → sessionStorage 삭제
```

---

### [Case 10] 상품 카테고리 전체 클릭 시 선글라스로 이동

**문제** : 헤더 "전체" 메뉴 클릭 시 전체 상품이 아닌 선글라스 카테고리로 이동  
**원인** : Vue Router `watch`가 쿼리 파라미터 없을 때 이전 상태(`category=sunglasses`)를 그대로 유지  
**해결** : 쿼리 파라미터 없을 때 `all`로 명시적 초기화

```
// Before — 쿼리 없으면 기존 값 유지
filters.value.category = q.category

// After — 쿼리 없으면 'all'로 명시 초기화
filters.value.category = q.category || 'all'
```

---

### [Case 11] 토스페이먼츠 SDK `TossPayments is not defined` 오류

**문제** : 토스페이 결제 버튼 클릭 시 `TossPayments is not a function` 오류 발생  
**원인** : Vue 컴포넌트 내에서 전역 객체 `TossPayments`를 직접 참조 → 빌드 환경에서 인식 불가  
**해결** : `window.TossPayments`로 명시적 전역 접근

```js
// Before — 빌드 후 인식 불가
const tossPayments = TossPayments(clientKey)

// After — window 전역 객체로 명시 접근
const tossPayments = window.TossPayments(clientKey)
```

---

### [Case 12] 토스페이먼츠 v2 API `requestPayment is not a function` 오류

**문제** : `window.TossPayments` 호출 후 `requestPayment is not a function` 오류 발생  
**원인** : 토스페이 SDK v2에서 API 구조 변경 — v1 방식(`requestPayment("카드", {...})`)이 v2에서 제거됨  
**해결** : v2 방식으로 변경 — `payment()` 객체 생성 후 `requestPayment()` 호출

```js
// Before — v1 방식 (v2에서 동작 안 함)
const tossPayments = window.TossPayments(clientKey)
await tossPayments.requestPayment("카드", { ... })

// After — v2 방식
const tossPayments = await window.TossPayments(clientKey)
const payment = tossPayments.payment({
  customerKey: store.user.loginId
})
await payment.requestPayment({
  method: "CARD",
  amount: { currency: "KRW", value: totalWithShipping.value },
  orderId,
  orderName,
  successUrl: window.location.origin + "/web03/payment/success",
  failUrl:    window.location.origin + "/web03/payment/fail",
})
```

---

### [Case 13] API 키 GitHub 노출 방지 — `application.yml` gitignore 처리

**문제** : 토스페이 API 키가 포함된 `application.yml`이 GitHub에 그대로 노출될 위험  
**원인** : `.gitignore` 미설정으로 민감 정보가 원격 저장소에 push됨  
**해결** : `.gitignore`에 `application.yml` 추가 + `git rm --cached`로 원격에서 제거

```bash
# .gitignore에 추가
echo "shop_spring_boot/shop/src/main/resources/application.yml" >> .gitignore

# 원격 저장소에서 캐시 제거 (로컬 파일은 유지)
git rm --cached shop_spring_boot/shop/src/main/resources/application.yml
git add .
git commit -m "chore: application.yml gitignore 처리 (키 보안)"
git push origin main
```

```yaml
# application.yml — 코드에 직접 하드코딩 대신 설정 파일에서 관리
toss:
  client-key: ${TOSS_CLIENT_KEY}
  secret-key:  ${TOSS_SECRET_KEY}
```

---

### [Case 14] 위시리스트 새로고침 시 초기화

**문제** : 위시리스트에 상품을 담아도 새로고침하면 목록이 비워짐  
**원인** : 위시리스트가 Pinia 스토어의 메모리 상태로만 존재 — DB 저장도, localStorage 저장도 없어 페이지가 새로 로드되면 상태가 초기화됨  
**해결** : 장바구니와 동일한 구조로 `wishlists` 테이블 및 API(`WishlistController`/`WishlistService`/`WishlistRepository`)를 신설하고, 로그인 시 서버에서 위시리스트를 불러오도록 변경. `(login_id, item_id)` 유니크 제약으로 중복 담기 방지

```java
// Wishlist Entity — Cart와 동일한 패턴
@Table(name = "wishlists", uniqueConstraints = @UniqueConstraint(columnNames = {"loginId", "itemId"}))
public class Wishlist {
    private Integer id;
    private String loginId;
    private Integer itemId;
    private LocalDateTime created;
}
```

```js
// store/shop.js — 서버에는 itemId 목록만 저장, 화면 표시용 전체 상품 정보는 조인해서 구성
const wishlistProducts = computed(() =>
  wishlist.value.map(w => products.value.find(p => p.id === w.itemId)).filter(Boolean)
)
```

---

### [Case 15] 이미지 업로드 확장자 검증 누락

**문제** : 상품 이미지 업로드 API가 파일 확장자/타입을 검증하지 않아 임의 파일 업로드 가능  
**원인** : `saveImage()`가 `MultipartFile`의 원본 파일명을 그대로 저장 파일명에 사용하고, 확장자·Content-Type 화이트리스트가 없었음  
**해결** : 이미지 MIME 타입·확장자 화이트리스트 검증 추가, 저장 파일명은 원본 파일명을 배제하고 `UUID + 검증된 확장자`로만 구성(경로 조작 가능성도 함께 제거)

```java
// Before — 원본 파일명을 그대로 사용, 검증 없음
String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();

// After — 화이트리스트 검증 + 원본 파일명 미사용
if (!ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType)) throw new IllegalArgumentException(...);
if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) throw new IllegalArgumentException(...);
String filename = UUID.randomUUID() + "." + extension;
```

---

### [Case 16] 비밀번호 찾기 API가 임시 비밀번호를 응답으로 노출

**문제** : `/api/member/find-pw` 응답 body에 생성된 임시 비밀번호가 그대로 담겨 반환됨  
**원인** : 이메일 발송 기능 없이 응답으로만 임시 비밀번호를 전달하도록 구현되어, `loginId`+`email`만 알면 누구나 계정 비밀번호를 탈취 가능한 상태였음  
**해결** : `spring-boot-starter-mail` 추가 후 임시 비밀번호를 이메일로만 발송, API 응답에서는 완전히 제거

```java
// Before
return tempPw; // 컨트롤러가 그대로 응답에 포함

// After
mailSender.send(message); // 이메일로만 전달, 응답은 success 여부만
```

---

### [Case 17] DB 비밀번호 하드코딩 및 git 히스토리 노출

**문제** : 초기 커밋의 `application.yml`에 DB 비밀번호가 평문으로 포함되어 GitHub 공개 저장소 히스토리에 영구 노출됨. 이후 `.gitignore` 처리(Case 13)는 향후 추적만 막았을 뿐 과거 커밋 내용은 그대로 남아있었음  
**원인** : 설정 파일에 비밀번호를 직접 하드코딩하는 구조였고, `application.properties`(실제 사용 파일)도 `.gitignore`에서 누락되어 있었음  
**해결** :
1. 실제 DB 비밀번호를 새 값으로 로테이션 (과거 노출된 값 무효화)
2. `application.properties`를 `.gitignore`에 추가
3. FORME 프로젝트와 동일하게, 비밀번호를 파일에 직접 쓰지 않고 systemd `Environment=DB_PASSWORD=...`로 주입, `application.properties`에는 `${DB_PASSWORD}` 자리표시자만 남김
4. 로컬/신규 클론 환경을 위해 `application.properties.example` 템플릿을 커밋

```properties
# application.properties (git에 커밋되지 않음)
spring.datasource.password=${DB_PASSWORD}
```

```ini
# /etc/systemd/system/gm-backend.service
Environment=DB_PASSWORD=실제_비밀번호
```

---

### [Case 18] 서버측 권한 검증 부재 — 전체 API 인가(Authorization) 우회 가능

**문제** : `SecurityConfig`가 거의 모든 API를 `permitAll()`로 열어두고, 로그인 검사용 `LoginCheckInterceptor`는 어디에도 등록되어 있지 않아 실제로는 동작하지 않는 상태였음. 로그인/관리자 여부는 Vue 프론트엔드(`store.isLoggedIn`, `store.isAdmin`)가 버튼을 숨기는 화면 처리로만 구현되어 있었고, 로그인 자체도 서버 세션 없이 로그인 응답을 `localStorage`에 저장하는 방식이라 서버는 요청자가 누구인지 전혀 알 수 없었음  
**원인** : "(관리자)" 주석만 달려 있을 뿐 컨트롤러 어디에도 실제 검증 코드가 없었음. 그 결과 `PUT /api/member/role/{loginId}`로 아무나 자기 계정을 관리자로 승격시키거나, `GET /api/member/list`로 전체 회원 PII를 비로그인 상태로 조회하거나, `GET /api/inquiry/all`로 전체 고객 문의를 열람하는 등 curl로 직접 호출하면 그대로 뚫리는 상태였음  
**해결** :
1. 로그인 성공 시 `HttpSession`에 `loginId`/`role` 저장(로그아웃 시 세션 무효화)
2. `SessionAuth` 헬퍼(`isAdmin`, `isSelfOrAdmin`, `isLoggedIn`)를 만들고, 관리자 전용/본인 전용 API 전체(회원·상품·공지·QnA·주문·문의·리뷰·장바구니·위시리스트·커뮤니티 게시판)에 서버측 검증 추가
3. 세션 쿠키에 `HttpOnly` + `SameSite=Lax` 설정 추가

```java
// Before — 컨트롤러에 실제 검증 없음, 주석만 존재
// role 변경 (관리자)
@PutMapping("/role/{loginId}")
public ResponseEntity<?> updateRole(@PathVariable String loginId, @RequestBody Map<String, String> body) {
    memberService.updateRole(loginId, body.get("role"));
    return ResponseEntity.ok(Map.of("success", true));
}

// After — 세션 기반 서버측 검증 추가
@PutMapping("/role/{loginId}")
public ResponseEntity<?> updateRole(@PathVariable String loginId, @RequestBody Map<String, String> body, HttpServletRequest request) {
    if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
    memberService.updateRole(loginId, body.get("role"));
    return ResponseEntity.ok(Map.of("success", true));
}
```

---

### [Case 19] `GENERATED ALWAYS AS IDENTITY` 컬럼 저장 시 회원가입 등 신규 등록 전체 실패

**문제** : 위 Case 18 배포 후 실제 동작 테스트 중 회원가입 API가 `cannot insert a non-DEFAULT value into column "id"` 오류로 500 실패하는 것을 발견. 같은 방식으로 대조해보니 `members`뿐 아니라 `notices`/`qnas`/`inquiries`/`boards`/`comments` 6개 테이블 모두 신규 저장(`save()`)이 전부 실패하는 상태였음(기존 데이터 조회는 정상 동작해 겉으로는 드러나지 않았음)  
**원인** : 해당 6개 테이블의 `id` 컬럼이 DB에서 `GENERATED ALWAYS AS IDENTITY`(애플리케이션이 값을 지정하면 무조건 거부)로 생성되어 있는데, 대응하는 엔티티들은 `@GeneratedValue`에 전략을 명시하지 않아 Hibernate 6 기본값(SEQUENCE 계열)이 적용되어 Java 쪽에서 자체적으로 ID를 생성해 INSERT에 포함시키려 했고, DB가 이를 거부함. `items`/`reviews`는 과거 Case 3에서 이미 `IDENTITY` 전략으로 맞춰져 있어 문제 없었음  
**해결** : 6개 엔티티(`Member`, `Notice`, `Qna`, `Inquiry`, `Board`, `Comment`) 모두 `Item`/`Review`와 동일하게 `@GeneratedValue(strategy = GenerationType.IDENTITY)`로 명시

```java
// Before — Hibernate 6 기본 전략이 identity 컬럼과 충돌
@Id
@GeneratedValue
private Integer id;

// After — DB의 GENERATED ALWAYS AS IDENTITY와 일치시킴
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;
```

실제 회원가입 → 로그인 → 문의 등록 → 위시리스트 추가 → 탈퇴까지 curl로 재현하여 정상 동작 확인.

---

### [Case 20] 로그인 API에 brute-force 방어 부재

**문제** : `/api/member/login`에 시도 횟수 제한이 전혀 없어 봇/스크립트가 특정 계정 비밀번호를 무제한으로 대입 시도할 수 있는 상태였음  
**원인** : 로그인 성공/실패만 판별할 뿐, 실패 횟수를 추적하는 로직이 없었음  
**해결** : 계정(loginId) 단위로 연속 실패 횟수를 추적하는 `LoginAttemptService`를 추가 — 5회 연속 실패 시 5분간 잠금(HTTP 429), 로그인 성공 시 카운트 초기화. 단일 인스턴스 배포라 인메모리(`ConcurrentHashMap`)로 충분

```java
if (loginAttemptService.isLocked(loginId)) {
    return ResponseEntity.status(429).body(Map.of("success", false, "message", "..."));
}
Member member = memberService.find(loginId, body.get("loginPw"));
if (member != null) {
    loginAttemptService.loginSucceeded(loginId);
    ...
}
loginAttemptService.loginFailed(loginId);
```

curl로 5회 연속 실패 → 6번째 429 확인, 무관한 다른 계정은 영향 없음(계정별 독립 추적) 확인.

---

### [Case 21] 컨트롤러 예외 메시지가 내부 SQL/스택 정보를 그대로 노출

**문제** : 거의 모든 컨트롤러가 `catch (Exception e)` 블록에서 `e.getMessage()`를 그대로 응답에 담아 반환(45곳). Case 19 재현 테스트 중 실제로 `cannot insert a non-DEFAULT value into column "id"` 같은 원본 SQL 오류 문구가 클라이언트에 그대로 노출되는 것을 확인 — 테이블/컬럼명 등 내부 구조를 정찰하는 데 악용될 수 있음  
**원인** : 컨트롤러 단에서 예외 종류를 구분하지 않고 메시지를 그대로 전달하도록 일괄 작성되어 있었음  
**해결** : 공통 헬퍼 `ApiError.badRequest(e)`를 추가해 `DataAccessException`(DB/JPA 계층 예외)만 서버 로그로 남기고 클라이언트에는 일반화된 메시지로 응답하도록 하고, 45곳의 `e.getMessage()` 호출을 전부 교체. 서비스 코드에서 직접 던지는 업무 예외(예: "아이디 또는 이메일이 일치하지 않습니다")는 기존처럼 그대로 노출되어 UX는 그대로 유지됨

```java
// Before — 모든 예외를 그대로 노출
return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));

// After — DB 계층 예외만 서버 로그로 격리, 응답은 일반화
return ApiError.badRequest(e);
```

중복 가입 시도(DB unique 제약 위반)로 재현 테스트 → 클라이언트에는 "요청을 처리하지 못했습니다..." 일반 메시지만 노출되고, 서버 로그에는 `DataIntegrityViolationException` 상세가 남는 것 확인. 존재하지 않는 계정으로 비밀번호 찾기 시도 시 기존 안내 메시지는 그대로 노출되는 것도 확인.

---

### [Case 22] 회원가입/비밀번호 변경에 서버측 비밀번호 정책 부재

**문제** : `/api/member/register`, `/api/member/update/{loginId}`가 비밀번호 길이·복잡도를 전혀 검증하지 않아 `a`, `1234` 같은 값도 그대로 암호화되어 저장 가능했음. 프론트엔드도 8자 이상 여부만 확인하고 있었음  
**원인** : 서비스 계층에 비밀번호 정책 검증 로직이 없었음  
**해결** : 공통 검증기 `PasswordPolicy`를 추가해 8자 이상 + 영문 소문자·숫자·특수문자 각 최소 1개 포함을 서버에서 강제(`BaseMemberService.save/update`). 프론트엔드(회원가입, 마이페이지 비밀번호 변경)에도 동일 기준으로 사전 검증 추가해 불필요한 왕복 없이 즉시 안내

```java
public static void validate(String password) {
    if (password == null || password.length() < 8) {
        throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
    }
    if (!LOWERCASE.matcher(password).find()
            || !DIGIT.matcher(password).find()
            || !SPECIAL.matcher(password).find()) {
        throw new IllegalArgumentException("비밀번호는 영문 소문자, 숫자, 특수문자를 모두 포함해야 합니다.");
    }
}
```

curl로 소문자만/8자 미만/특수문자 없음 3가지 위반 케이스 모두 거부, 정책 충족 비밀번호는 정상 가입되는 것 확인.

---

### [Case 23] CSRF 방어 부재 + 커뮤니티 게시판 화이트리스트 경로 오타로 전체 차단

**문제 1(CSRF)** : `SecurityConfig`가 `.csrf(disable)`로 CSRF 방어를 완전히 꺼놓은 상태였음. `SameSite=Lax` 세션 쿠키(Case 18)로 상당 부분 방어되긴 하지만 토큰 기반 방어는 아니었음  
**문제 2(경로 오타, CSRF 작업 중 같은 파일에서 발견)** : 화이트리스트에 `/api/board/**`가 등록되어 있었는데 실제 컨트롤러는 `@RequestMapping("/api/community")`라서 전혀 매칭되지 않음 → 커뮤니티 게시판 전체(조회 포함)가 `anyRequest().authenticated()`에 걸려 관리자를 포함해 아무도 접근 못 하는 상태였음  
**해결** :
1. Spring Security 내장 CSRF를 다시 활성화 — `CookieCsrfTokenRepository`로 `XSRF-TOKEN` 쿠키를 발급하고, SPA에서도 쿠키가 정상 내려가도록 매 요청마다 토큰 로드를 강제하는 `CsrfCookieFilter` 추가(Spring 공식 SPA 연동 가이드 패턴)
2. 화이트리스트의 `/api/board/**`를 실제 경로인 `/api/community/**`로 수정(안 쓰이던 `/api/comment/**` 항목 제거)
3. 프론트엔드는 `fetch`를 한 곳(`csrf.js`)에서 감싸 상태변경 요청(POST/PUT/DELETE)에 쿠키의 토큰 값을 `X-XSRF-TOKEN` 헤더로 자동 첨부 — 수십 곳의 개별 fetch 호출을 손대지 않음

```java
.csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
)
.addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
```

```js
// csrf.js — fetch 전역 래핑
if (MUTATING_METHODS.has(method)) {
  const token = getCookie('XSRF-TOKEN')
  headers.set('X-XSRF-TOKEN', token)
}
```

curl로 최초 GET → 쿠키 발급 → 토큰 없이 POST(403 차단) → 토큰 포함 POST(정상 처리) 확인. 회원가입 → 로그인 → 위시리스트 추가 → 커뮤니티 글쓰기 → 로그아웃 전 과정을 토큰 포함 요청으로 재현해 정상 동작 확인, `/api/community` 조회도 200으로 복구된 것 확인.

---

### [Case 24] 주문 금액이 결제수단과 무관하게 서버 검증 없이 클라이언트 값 그대로 저장됨 (가격 조작 가능)

**문제** : `POST /api/order`가 클라이언트가 보낸 `amount`를 어떤 검증도 없이 그대로 저장. `OrderItem`은 가격 필드조차 없어 상품 실제 가격과 결제 금액을 연결할 방법이 없었음. 토스 결제뿐 아니라 카카오/네이버/계좌이체 등 검증 주체가 없는 결제수단은 특히 취약 — 원하는 금액으로 주문을 만들 수 있는 구조였음(토스 결제 금액 서버 이중검증 부재로 시작했으나 조사 중 더 근본적인 문제로 확인)  
**부수 발견** : 서버 검증 기준을 정하려고 프론트엔드 금액 계산을 보니, 장바구니 합계(`cartTotal`)가 할인율을 전혀 반영하지 않고 원가로 계산되고 있었음(별개의 기존 버그)  
**해결** :
1. 프론트엔드 `cartTotal`이 상품 카드/상세 페이지와 동일한 규칙(할인가, 100원 단위 반올림)으로 할인을 반영하도록 수정
2. `BaseOrderService.save()`에 `validateAmount()`를 추가해 주문 항목의 실제 가격(할인 반영) 합계 + 배송비(5만원 미만 시 3천원)를 서버에서 재계산하고, 클라이언트가 보낸 `amount`와 다르면 주문 자체를 거부
3. 이 프로젝트는 상품 카탈로그가 DB(`items`, 관리자가 추가한 상품)와 정적 시드 파일(`products.json`, 초기 22개 상품)로 나뉘어 있어(Case 8), DB에 없는 상품 ID는 `StaticProductCatalog`(서버가 직접 배포하는 `products.json`을 읽어옴 — 클라이언트가 조작 불가)로 폴백 조회

```java
private void validateAmount(Integer amount, List<Map<String, Object>> items) {
    double subtotal = 0;
    for (Map<String, Object> item : items) {
        // DB(items)에 있으면 DB 가격, 없으면 정적 카탈로그(products.json) 가격 사용
        double unitPrice = discountRate > 0
                ? Math.round((price * (1 - discountRate / 100)) / 100.0) * 100
                : price;
        subtotal += unitPrice * quantity;
    }
    double expected = subtotal + (subtotal >= 50000 ? 0 : 3000);
    if (amount == null || Math.abs(expected - amount) > 1) {
        throw new IllegalArgumentException("주문 금액이 올바르지 않습니다.");
    }
}
```

curl로 검증: 정적 카탈로그 상품(정가 40만원, 무료배송) 정상금액 주문 성공 / 100원으로 조작한 주문 거부 / 존재하지 않는 상품 ID 거부 / DB 할인상품(20% 할인 반영 금액) 정상 주문 성공 / 할인 미반영 금액으로는 거부, 4가지 케이스 모두 확인.

<br>

---

## Database Schema

총 **12개 테이블**로 구성:

| 테이블 | 주요 설계 포인트 |
|--------|----------------|
| `members` | BCrypt 암호화, role(user/admin/banned), grade(브론즈~플래티넘) |
| `items` | 실시간 재고, 할인율, badge, category, details_json(세부정보 JSON) |
| `orders` | 상태(주문접수/배송중/배송완료/취소) |
| `order_items` | orders : order_items = 1:N, 주문 시점 상품명/색상/사이즈 스냅샷 저장 |
| `reviews` | 배송완료 주문 확인 후 작성 가능, 별점(1~5) |
| `carts` | 로그인 기반 DB 저장 (새로고침 후에도 유지) |
| `wishlists` | 로그인 기반 DB 저장, (login_id, item_id) 유니크 제약으로 중복 방지 |
| `notices` | 중요 공지 구분 |
| `qnas` | 카테고리별 분류 |
| `inquiries` | 1:1 문의, 관리자 답변 포함 |
| `boards` | 커뮤니티 게시판 |
| `comments` | 게시글 댓글 |

<br>

---

## Getting Started

### Docker Hub에서 바로 실행 (권장)

```bash
# 이미지 받기
docker pull dongyun12/shop-app:latest

# 컨테이너 실행
docker run -it --name ubuntu01 -p 8086:8086 dongyun12/shop-app:latest bash

# 컨테이너 내부에서 실행
service postgresql start
cd /root
java -jar shop-0.0.1-SNAPSHOT.jar &
```

접속 URL: `http://localhost:8086/web03/`

---

### 로컬 빌드 방법

**Prerequisites**
- Java 21+
- Node.js 20+
- Docker

**1. 환경 설정 파일 준비**
```bash
cd shop
cp src/main/resources/application.properties.example src/main/resources/application.properties
# application.properties에서 토스/메일 값 채우기, DB 비밀번호는 환경변수로 주입
export DB_PASSWORD=본인_postgres_비밀번호
```

**2. Vue 빌드**
```bash
cd vue-shop
npm install
npm run build
```

**3. 빌드 결과물 복사 (Windows)**
```bash
xcopy /E /Y dist\* ..\shop\src\main\resources\static\web03\
```

**4. Spring Boot 빌드**
```bash
cd shop
gradlew clean build -x test
```

**5. Docker 컨테이너에 배포**
```bash
docker cp build/libs/shop-0.0.1-SNAPSHOT.jar ubuntu01:/root/
docker exec -it ubuntu01 bash
pkill -f shop && sleep 2
java -jar /root/shop-0.0.1-SNAPSHOT.jar &
```

<br>

---

## Project Structure

```
Frontend (Vue.js 3)
└── src/
    ├── views/          # 페이지 컴포넌트 (Home, Product, Cart, Checkout, MyPage, Admin 등)
    ├── components/     # 공통 컴포넌트 (AppHeader, ProductCard)
    ├── store/          # Pinia 전역 상태 (상품, 장바구니, 회원)
    └── router/         # Vue Router (Navigation Guard 포함)

Backend (Spring Boot 3.2)
└── src/main/java/com/example/demo/
    ├── member/         # 회원 도메인
    ├── item/           # 상품 도메인
    ├── cart/           # 장바구니 도메인
    ├── order/          # 주문 도메인
    ├── review/         # 리뷰 도메인
    ├── board/          # 커뮤니티 도메인
    ├── notice/         # 공지사항 도메인
    ├── qna/            # QnA 도메인
    ├── inquiry/        # 고객문의 도메인
    ├── wishlist/       # 위시리스트 도메인
    └── config/         # Security, Web, JPA, SPA 설정
```

<br>

---

## TODO

- JWT Refresh Token 도입 (현재 BCrypt + Security 기반)
- Redis 캐싱 레이어 추가 (상품 목록 조회 성능 개선)
- CI/CD 파이프라인 구성 (GitHub Actions → Docker Hub → 자동 배포)
- 단위 테스트 커버리지 확보 (JUnit5 + Mockito)
- 실제PG사 결제 연동(토스페이먼츠)

<br>

---

## Related Repository

| 구분 | 링크 |
|------|------|
| Frontend | [portfolio_shop_frontend](https://github.com/dhwldrjekd1/portfolio_shop_frontend) |
| Backend | [portfolio_shop_spring_boot](https://github.com/dhwldrjekd1/portfolio_shop_spring_boot) |

<br>

---

<p align="center">
  <i>Inspired by Gentle Monster — Less, but better.</i>
</p>
