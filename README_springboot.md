# Gentle Monster Inspired - 쇼핑몰 포트폴리오 (Backand)

> 젠틀몬스터를 디자인 레퍼런스로 참고하여, 전체 기능과 아키텍처는 직접 설계 및 구현한 풀스택 쇼핑몰 포트폴리오입니다.
>
> **Backand Repository:** [portfolio_shop_spring_boot](https://github.com/dhwldrjekd1/portfolio_shop_spring_boot)

---

## 📌 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | Gentle Monster Inspired 쇼핑몰 |
| 개발 기간 | 2026.03 |
| 개발자    | 최동윤 |
| 개발 인원 | 1인 (풀스택) |
| 배포 환경 | Docker (Ubuntu 컨테이너) |
| 접속 URL | `http://localhost:8086/web03/` |

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| Framework | Spring Boot 3.2.0 (Java 21) |
| 보안 | Spring Security (BCrypt, SecurityFilterChain) |
| ORM | Spring Data JPA + Hibernate |
| DB | PostgreSQL 14 |
| 문서화 | Swagger (SpringDoc OpenAPI 3) |
| 기타 | Lombok, Docker |

---

## 📁 프로젝트 구조

```
src/main/java/com/example/demo/
├── member/           # 회원 도메인
│   ├── entity/Member.java
│   ├── service/MemberService.java
│   ├── service/BaseMemberService.java
│   └── controller/MemberController.java
├── item/             # 상품 도메인
├── cart/             # 장바구니 도메인
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

### 회원 API
```
POST /api/member/register     회원가입
POST /api/member/login        로그인 (BCrypt 검증)
GET  /api/member/info/{id}    회원정보 조회
PUT  /api/member/update/{id}  회원정보 수정
DELETE /api/member/delete/{id} 회원탈퇴
POST /api/member/find-pw      비밀번호 찾기
GET  /api/member/list         회원 목록 (관리자)
PUT  /api/member/grade/{id}   등급 변경 (관리자)
PUT  /api/member/role/{id}    권한 변경 (관리자)
```

### 상품 API
```
GET    /api/item               전체 상품 조회
GET    /api/item/{id}          단일 상품 조회
POST   /api/item               상품 등록 (관리자, multipart)
PUT    /api/item/{id}          상품 수정 (관리자, multipart)
DELETE /api/item/{id}          상품 삭제 (관리자)
PUT    /api/item/{id}/stock    재고 수정
PUT    /api/item/{id}/discount 할인율 수정
PUT    /api/item/{id}/details  세부정보 수정 (JSON)
```

### 장바구니 API
```
GET    /api/cart/{loginId}       장바구니 조회
POST   /api/cart                 장바구니 추가
PUT    /api/cart/{id}            수량 변경
DELETE /api/cart/{id}            개별 삭제
DELETE /api/cart/clear/{loginId} 전체 삭제
```

### 주문 API
```
GET  /api/order/{loginId}       회원 주문 목록
POST /api/order                 주문 생성
GET  /api/order/all             전체 주문 (관리자)
PUT  /api/order/{id}/status     상태 변경 (관리자)
PUT  /api/order/{id}/cancel     주문 취소 (일반회원)
DELETE /api/order/{id}          주문 삭제 (관리자)
```

### 리뷰 API
```
GET  /api/review/item/{itemId}                      상품별 리뷰
GET  /api/review/member/{loginId}                   회원별 리뷰
GET  /api/review/all                                전체 리뷰
POST /api/review                                    리뷰 등록
PUT  /api/review/{id}                               리뷰 수정
DELETE /api/review/{id}                             리뷰 삭제
GET  /api/review/check/purchased/{loginId}/{itemId} 구매 확인
GET  /api/review/check/reviewed/{loginId}/{itemId}  리뷰 작성 확인
```

---

## DB 테이블

```sql
-- 11개 테이블
items        -- 상품 (badge, category, details_json 포함)
members      -- 회원 (grade, role 포함)
carts        -- 장바구니
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
    .requestMatchers("/api/order/**").permitAll()
    // ... 각 API 허용
    .anyRequest().authenticated()
  )
  .formLogin(AbstractHttpConfigurer::disable)
  .httpBasic(AbstractHttpConfigurer::disable);
```

---

## application.yml

```yaml
server:
  port: 8086

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shop
    username: postgres
    password: 1004
  jpa:
    hibernate:
      ddl-auto: update
  security:
    user:
      name: none
      password: none

file:
  upload-dir: /root/uploads
```

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
| Frontend | [portfolio_shop](https://github.com/dhwldrjekd1/portfolio_shop) |
