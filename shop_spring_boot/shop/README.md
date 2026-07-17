# Gentle Monster Inspired - 쇼핑몰 포트폴리오 (Backend)

> 젠틀몬스터를 디자인 레퍼런스로 참고하여, 전체 기능과 아키텍처는 직접 설계 및 구현한 풀스택 쇼핑몰 포트폴리오입니다.

---

## 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | Gentle Monster Inspired 쇼핑몰 |
| 개발 기간 | 2026.03 |
| 개발자 | 최동윤 |
| 개발 인원 | 1인 (풀스택) |
| 배포 환경 | Ubuntu Server + Spring Boot |
| 접속 URL | https://gm.dyy.kr |

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Framework | Spring Boot 3 |
| ORM | Spring Data JPA |
| DB | MySQL |
| 빌드 도구 | Gradle |
| 보안 | Spring Security (세션 기반 인증) |
| 결제 | 토스페이먼츠 API |
| 정적 리소스 | Vue 3 dist → Spring Boot static 내장 서빙 |

---

## 프로젝트 구조

```
src/main/java/com/example/demo/
├── member/          # 회원가입/로그인/로그아웃, 정보 조회·수정·탈퇴, 등급 관리
├── item/            # 상품 목록, 상세, 필터, 재고
├── cart/            # 장바구니 CRUD
├── order/           # 주문 생성(서버측 금액 검증), 취소, 관리자 주문 관리
├── payment/         # 토스페이먼츠 결제 연동 (TossConfig, TossController)
├── review/          # 리뷰 작성·수정·삭제, 중복 방지
├── board/           # 커뮤니티 게시판
├── notice/          # 공지사항
├── qna/             # QnA 게시판
├── inquiry/         # 고객문의
├── common/          # SessionAuth, ApiError, PasswordPolicy 등 공통 헬퍼
└── config/          # Security(CSRF 포함), JPA, Web, SPA 라우팅 설정
```

---

## 구현 기능

### 회원
- 회원가입 / 로그인 / 로그아웃 (세션 기반)
- 회원 정보 조회·수정·탈퇴
- 비밀번호 찾기 (임시 비밀번호 발급)
- 구매금액 기반 자동 등급 업데이트 (브론즈 / 실버 / 골드 / 플래티넘)
- 세션 기반 서버측 권한 검증 (`SessionAuth`) — 관리자 전용 API 및 본인 소유 데이터 API를 컨트롤러 단에서 직접 검증

### 상품
- 상품 목록 (카테고리 / 뱃지 / 가격 / 평점 필터, 정렬)
- 상품 상세 (색상·사이즈 옵션, 재고 실시간 반영)
- 할인가·할인율 계산

### 장바구니 / 결제
- 장바구니 담기 / 수량 변경 / 삭제
- 토스페이먼츠 결제 승인 API 연동
- 주문 취소 (주문접수·배송중 상태만 허용)

### 리뷰
- 배송완료 주문 확인 후 리뷰 작성
- 동일 상품 중복 리뷰 방지
- 리뷰 수정·삭제, 평균 별점 실시간 반영

### 게시판
- 공지사항 / QnA / 고객문의 / 커뮤니티 CRUD

### 관리자
- 회원 목록 / 주문 관리 / 재고 관리
- 판매 통계 / 상품 관리 / 리뷰 관리

---

## 빌드 및 배포

```bash
# JAR 빌드
./gradlew bootJar

# 실행
java -jar build/libs/shop-0.0.1-SNAPSHOT.jar
```

> Vue 3 프론트엔드 dist 파일은 `src/main/resources/static/web03/`에 위치하며 Spring Boot가 함께 서빙합니다.

---

## 연관 레포지토리

| 구분 | 링크 |
|------|------|
| Frontend | [portfolio_shop_frontend](https://github.com/dhwldrjekd1/portfolio_shop_frontend) |
