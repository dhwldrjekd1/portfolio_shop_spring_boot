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

<br>

---

## Database Schema

총 **11개 테이블**로 구성:

| 테이블 | 주요 설계 포인트 |
|--------|----------------|
| `members` | BCrypt 암호화, role(user/admin/banned), grade(브론즈~플래티넘) |
| `items` | 실시간 재고, 할인율, badge, category, details_json(세부정보 JSON) |
| `orders` | 상태(주문접수/배송중/배송완료/취소) |
| `order_items` | orders : order_items = 1:N, 주문 시점 상품명/색상/사이즈 스냅샷 저장 |
| `reviews` | 배송완료 주문 확인 후 작성 가능, 별점(1~5) |
| `carts` | 로그인 기반 DB 저장 (새로고침 후에도 유지) |
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

**1. Vue 빌드**
```bash
cd vue-shop
npm install
npm run build
```

**2. 빌드 결과물 복사 (Windows)**
```bash
xcopy /E /Y dist\* ..\shop\src\main\resources\static\web03\
```

**3. Spring Boot 빌드**
```bash
cd shop
gradlew clean build -x test
```

**4. Docker 컨테이너에 배포**
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
| Frontend | [portfolio_shop](https://github.com/dhwldrjekd1/portfolio_shop) |
| Backend | [portfolio_shop_spring_boot](https://github.com/dhwldrjekd1/portfolio_shop_spring_boot) |

<br>

---

<p align="center">
  <i>Inspired by Gentle Monster — Less, but better.</i>
</p>
