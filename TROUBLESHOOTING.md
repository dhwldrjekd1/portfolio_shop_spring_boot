# Gentle Monster Inspired 쇼핑몰 포트폴리오

---

## 1. 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | Gentle Monster 클론 쇼핑몰 |
| 개발 기간 | 2026.03 |
| 개발자    | 최동윤 |
| 개발 인원 | 1인 (풀스택) |
| 기술 스택 | Vue.js 3, Spring Boot 3.2, PostgreSQL, Docker |
| 배포 환경 | Docker Ubuntu 컨테이너 |

---

## 2. 기술 스택

### Frontend
- Vue.js 3 (Composition API, script setup)
- Pinia (전역 상태 관리)
- Vue Router 4 (SPA 라우팅, 라우터 가드)
- Vite (빌드 도구)
- Bootstrap 5 / Bootstrap Icons

### Backend
- Spring Boot 3.2.0 (Java 21)
- Spring Security (BCrypt 암호화, SecurityFilterChain)
- Spring Data JPA + Hibernate
- PostgreSQL 14
- Lombok, Swagger (SpringDoc OpenAPI 3)

### Infra
- Docker (Ubuntu 컨테이너)
- PostgreSQL (Docker 내부 실행)

---

## 3. 구현 기능 목록

### 회원 기능
- 회원가입 / 로그인 / 로그아웃
- BCrypt 비밀번호 암호화
- 마이페이지 (회원정보 조회/수정, 회원탈퇴)
- 비밀번호 찾기 (임시 비밀번호 발급)
- 구매금액 기반 자동 등급 업데이트
  - 브론즈(0~50만원) / 실버(50~100만원) / 골드(100~150만원) / 플래티넘(150만원+)
- 강퇴 회원 로그인 차단

### 상품 기능
- 상품 목록 (카테고리/뱃지/가격/평점 필터, 정렬)
- 상품 상세 (이미지 갤러리, 색상/사이즈 선택)
- 할인가 표시 (원가 취소선 + 할인가 + %OFF, 백원단위 반올림)
- DB 기반 재고/할인율/가격 실시간 반영
- 상품별 DB 평균 별점 표시

### 장바구니
- 장바구니 담기 (색상/사이즈 포함)
- 수량 변경 / 개별 삭제 / 전체 삭제
- 새로고침 후에도 유지 (DB 기반)
- 비로그인 시 로그인 모달 표시

### 주문/결제
- 3단계 결제 프로세스
- 배송 메모 (선택형 + 직접 입력)
- 주문완료 페이지 (주문 상품 이미지 표시)
- 주문 취소 (주문접수/배송중 상태만 가능)
- 배송완료 시 재고 자동 차감 + 등급 재계산
- 주문 취소 시 재고 복구 + 등급 재계산

### 리뷰
- 배송완료 주문 확인 후 리뷰 작성 가능
- 동일 상품 중복 리뷰 방지
- 리뷰 수정 / 삭제
- DB 기반 평균 별점 상품 페이지 반영

### 고객지원
- 공지사항: 관리자 등록/수정/삭제
- QnA: 카테고리 필터, 관리자 등록/수정/삭제
- 고객문의: 일반회원 등록/수정/삭제, 관리자 답변 등록

### 커뮤니티
- 게시글 등록/수정/삭제
- 댓글 등록/수정/삭제
- 검색 (제목/내용/작성자)

### 관리자 대시보드 (6개 탭)
- 회원 목록: 검색, 등급 변경, 권한 변경, 강퇴
- 주문 관리: 검색, 기간 필터, 페이징(10건), 상태 변경, 삭제
- 재고 관리: 재고/할인율 수정
- 판매 관리: 매출 통계 카드, 상품별 판매량, 기간 필터
- 상품 관리: 등록/수정/삭제, 이미지 업로드(파일/URL), 세부정보 수정
- 리뷰 관리: 목록, 수정, 삭제

---

## 4. 트러블슈팅

### 문제 1: 포트 8086 중복 실행 오류
**상황:** Docker 컨테이너에서 JAR를 재실행할 때 기존 프로세스가 종료되지 않아 포트 충돌 발생

**원인:** `java -jar` 실행 시 기존 프로세스가 백그라운드에서 계속 동작

**해결:**
```bash
pkill -f shop
sleep 2
java -jar shop-0.0.1-SNAPSHOT.jar &
```

---

### 문제 2: 이미지 업로드 후 경로 NOT NULL 오류
**상황:** 관리자 상품 등록 시 이미지 없이 등록하면 DB에서 NOT NULL 제약 조건 위반 오류 발생

**원인:** `items` 테이블의 `image_path` 컬럼에 NOT NULL 제약 조건이 걸려 있었음

**해결:**
```sql
ALTER TABLE items ALTER COLUMN image_path DROP NOT NULL;
```
`Item.java` 엔티티에서도 `nullable = true` 처리

---

### 문제 3: 상품 등록 시 duplicate key 오류
**상황:** 관리자가 상품을 등록할 때 `duplicate key value violates unique constraint "items_pkey"` 오류 발생

**원인:** `Item.java`의 `@GeneratedValue` 전략이 `SEQUENCE`로 설정되어 기존 수동 입력된 데이터와 시퀀스가 불일치

**해결:**
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```
`IDENTITY` 전략으로 변경하여 DB의 AUTO INCREMENT에 위임

---

### 문제 4: Spring Security 적용 후 모든 API 401 오류
**상황:** `spring-boot-starter-security` 의존성 추가 후 로그인, 상품 조회 등 모든 API 호출 시 401 오류 발생

**원인:** Spring Security 기본 설정이 모든 요청에 인증을 요구함

**해결:**
```java
// SecurityConfig.java에 permitAll() 설정 추가
.requestMatchers("/api/member/**").permitAll()
.requestMatchers("/api/item/**").permitAll()
// ... 각 API 경로별 허용 설정
```

---

### 문제 5: Spring Security 기본 비밀번호 자동 생성 경고
**상황:** Spring Security 적용 후 `Using generated security password: xxxxxxxx` 경고 발생, 이로 인해 로그인 API 400 오류

**원인:** 기본 `UserDetailsService`가 활성화되어 랜덤 비밀번호를 생성하고 HTTP Basic 인증을 요구

**해결:**
```java
// 빈 UserDetailsService 등록으로 기본 비밀번호 생성 방지
@Bean
public UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager();
}
```
```yaml
# application.yml에도 추가
spring:
  security:
    user:
      name: none
      password: none
```

---

### 문제 6: 배송완료 후에도 리뷰 작성 폼이 계속 표시됨
**상황:** 리뷰를 이미 작성했음에도 불구하고 상품 상세 페이지에서 리뷰 작성 폼이 계속 표시됨

**원인:** `hasPurchased`와 `hasReviewed` 체크 API 연동 로직에서 `canWriteReview` computed 조건이 잘못 설정됨

**해결:**
```js
// hasPurchased: 배송완료 주문 있음
// hasReviewed: 이미 리뷰 작성함
const canWriteReview = computed(() => hasPurchased.value && !hasReviewed.value)
```

---

### 문제 7: products.json 하드코딩 리뷰가 DB와 혼재
**상황:** 상품 상세 페이지에 DB 리뷰와 products.json의 하드코딩 리뷰가 동시에 표시됨

**원인:** store.js에서 `reviews` state가 products.json 데이터를 로드하고 있었음

**해결:**
- products.json에서 `reviews`, `rating`, `reviewCount` 필드 완전 제거
- `ProductDetailView.vue`에서 DB API(`/api/review/item/{id}`)로 리뷰 직접 조회
- store.js에서 `getReviewsByProductId` 함수 및 `reviews` state 제거

---

### 문제 8: 관리자 신규 등록 상품이 상품 페이지에 미표시
**상황:** 관리자가 신규 상품을 등록해도 상품 목록 페이지에 표시되지 않음

**원인:** `store.js`의 `fetchData()`가 `products.json`에 있는 상품만 표시하고 DB에만 있는 신규 상품은 무시

**해결:**
```js
// DB에만 있는 신규 등록 상품 별도 처리
const jsonIds = data.products.map(p => Number(p.id))
const dbOnly = items.filter(i => !jsonIds.includes(Number(i.id))).map(i => ({
  id: Number(i.id),
  name: i.name,
  category: i.category || 'all',
  // ... 기본값 설정
}))
products.value = [...jsonProducts, ...dbOnly]
```

---

### 문제 9: 주문완료 페이지에 상품 이미지 미표시
**상황:** 주문 완료 후 OrderCompleteView에 상품 이미지가 표시되지 않음

**원인:** `placeOrder()` 실행 시 `clearCart()`로 장바구니가 비워진 후 상품 정보에 접근 불가

**해결:** `clearCart()` 호출 전에 `sessionStorage`에 주문 정보 저장
```js
// clearCart() 전에 sessionStorage에 저장
const orderItems = store.cart.map(item => {
  const product = store.getProductById(item.itemId)
  return { name: product?.name, image: product?.images?.[0], ... }
})
sessionStorage.setItem('last_order_items', JSON.stringify(orderItems))
// OrderCompleteView에서 sessionStorage에서 읽어서 표시
```

---

### 문제 10: 상품 카테고리 필터 - 전체 클릭 시 선글라스로 이동
**상황:** 헤더의 "전체" 메뉴 클릭 시 전체 상품이 표시되어야 하는데 선글라스 카테고리로 이동됨

**원인:** `ProductView.vue`의 `watch`가 이전 쿼리 파라미터(`category=sunglasses`)를 유지하고 있었음

**해결:**
```js
watch(() => route.query, (q) => {
  // category 쿼리 없으면 'all'로 초기화
  filters.value.category = q.category || 'all'
})
```

---

## 5. DB 테이블 구조

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| items | 상품 | id, name, image_path, price, discount_rate, stock, category, badge, details_json |
| members | 회원 | id, login_id, login_pw(BCrypt), role, grade |
| carts | 장바구니 | id, login_id, item_id, quantity, color, size |
| orders | 주문 | id, login_id, address, payment, amount, status |
| order_items | 주문 상품 | id, order_id, item_id, item_name, color, size, quantity |
| reviews | 리뷰 | id, item_id, login_id, content, rating |
| notices | 공지사항 | id, title, content, important |
| qnas | QnA | id, category, question, answer |
| inquiries | 고객문의 | id, title, content, login_id, status, reply |
| boards | 커뮤니티 | id, title, content, login_id |
| comments | 댓글 | id, content, login_id, board_id |

---

## 6. 아키텍처

```
[브라우저]
    ↓ HTTP
[Vue.js SPA - /web03/]
    ↓ REST API
[Spring Boot (8086)]
    ↓ JPA
[PostgreSQL]

[Spring Security FilterChain]
- CSRF 비활성화
- BCrypt 암호화
- API 접근 권한 제어
- 기본 UserDetailsService 비활성화
```

---

## 7. 배포 구조

```
Windows (개발)
├── Vue.js 개발 → npm run build
├── dist/ → Spring Boot static/web03/
└── gradlew clean build → JAR

Docker (ubuntu01 컨테이너)
├── PostgreSQL (5432)
└── Spring Boot JAR (8086)
    └── /root/uploads/ (업로드 이미지)
```

---

## 8. 향후 개선 사항

- [ ] JWT 토큰 기반 인증 추가
- [ ] 실제 PG사 결제 연동 (카카오페이, 토스페이먼츠)
- [ ] AWS S3 이미지 업로드
- [ ] Redis 세션 캐싱
- [ ] CI/CD 파이프라인 구축
- [ ] 반응형 모바일 최적화
