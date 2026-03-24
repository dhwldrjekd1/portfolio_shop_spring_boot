-- =====================================================
-- Gentle Monster Inspired 쇼핑몰 - DB 테이블 정의
-- PostgreSQL 14
-- =====================================================


-- ===== 상품 테이블 =====
-- 관리자가 등록/수정/삭제하는 쇼핑몰 상품 정보
CREATE TABLE items (
    id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 상품 고유 ID (자동 증가)
    name          VARCHAR(255) NOT NULL,                            -- 상품명
    image_path    VARCHAR(500),                                     -- 이미지 경로 (파일 업로드 또는 외부 URL)
    price         DOUBLE PRECISION NOT NULL,                        -- 상품 가격
    discount_rate DOUBLE PRECISION DEFAULT 0,                       -- 할인율 (0~100, 0이면 할인 없음)
    stock         INTEGER NOT NULL DEFAULT 0,                       -- 재고 수량 (배송완료 시 자동 차감)
    description   VARCHAR(255),                                     -- 상품 설명
    badge         VARCHAR(50),                                      -- 뱃지 (NEW, BEST, SALE 등)
    category      VARCHAR(50),                                      -- 카테고리 (all, sunglasses, eyeglasses)
    details_json  TEXT,                                             -- 세부정보 JSON (소재, 렌즈 등 key-value)
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP               -- 등록일시
);


-- ===== 회원 테이블 =====
-- 일반회원 및 관리자 계정 정보
CREATE TABLE members (
    id             INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 회원 고유 ID (자동 증가)
    name           VARCHAR(50) NOT NULL,                             -- 회원 이름
    login_id       VARCHAR(50) NOT NULL UNIQUE,                      -- 로그인 아이디 (중복 불가)
    login_pw       VARCHAR(255) NOT NULL,                            -- 비밀번호 (BCrypt 암호화 저장)
    email          VARCHAR(100) UNIQUE,                              -- 이메일 (중복 불가)
    phone          VARCHAR(20),                                      -- 전화번호
    address        VARCHAR(255),                                     -- 기본 주소
    address_detail VARCHAR(255),                                     -- 상세 주소
    zipcode        VARCHAR(10),                                      -- 우편번호
    role           VARCHAR(20) DEFAULT 'user',                       -- 권한 (user, admin, banned)
    grade          VARCHAR(20) DEFAULT '브론즈',                     -- 등급 (브론즈/실버/골드/플래티넘, 구매금액 기반 자동 업데이트)
    created        TIMESTAMP DEFAULT CURRENT_TIMESTAMP               -- 가입일시
);


-- ===== 장바구니 테이블 =====
-- 회원별 장바구니 상품 목록 (로그인 상태에서 DB에 저장되어 새로고침 후에도 유지)
CREATE TABLE carts (
    id       SERIAL PRIMARY KEY,          -- 장바구니 항목 고유 ID
    login_id VARCHAR(255) NOT NULL,       -- 회원 로그인 아이디 (members.login_id 참조)
    item_id  INTEGER NOT NULL,            -- 상품 ID (items.id 참조)
    quantity INTEGER NOT NULL DEFAULT 1,  -- 수량
    color    VARCHAR(50),                 -- 선택한 색상
    size     VARCHAR(50),                 -- 선택한 사이즈
    created  TIMESTAMP NOT NULL           -- 장바구니 추가일시
);


-- ===== 주문 테이블 =====
-- 회원의 주문 정보 (배송지, 결제수단, 금액, 상태 포함)
CREATE TABLE orders (
    id          SERIAL PRIMARY KEY,                      -- 주문 고유 ID
    login_id    VARCHAR(255) NOT NULL,                   -- 주문자 로그인 아이디
    name        VARCHAR(50) NOT NULL,                    -- 수령인 이름
    address     VARCHAR(500) NOT NULL,                   -- 배송지 (우편번호 + 주소 + 배송메모 포함)
    payment     VARCHAR(10) NOT NULL,                    -- 결제수단 (card, kakao, naver, bank)
    card_number VARCHAR(16),                             -- 카드번호 (카드 결제 시)
    amount      INTEGER NOT NULL,                        -- 결제 금액 (배송비 포함)
    status      VARCHAR(50) NOT NULL DEFAULT '주문접수', -- 주문 상태 (주문접수/배송중/배송완료/취소)
    created     TIMESTAMP NOT NULL                       -- 주문일시
);


-- ===== 주문 상품 테이블 =====
-- 주문 1건에 포함된 상품 목록 (orders 테이블과 1:N 관계)
CREATE TABLE order_items (
    id        SERIAL PRIMARY KEY,         -- 주문 상품 고유 ID
    item_id   INTEGER NOT NULL,           -- 상품 ID (items.id 참조)
    quantity  INTEGER NOT NULL DEFAULT 1, -- 주문 수량
    item_name VARCHAR(255),               -- 주문 시점의 상품명 (상품 삭제 후에도 내역 유지)
    color     VARCHAR(50),                -- 선택한 색상
    size      VARCHAR(50),                -- 선택한 사이즈
    order_id  INTEGER NOT NULL            -- 주문 ID (orders.id 참조)
);


-- ===== 공지사항 테이블 =====
-- 관리자가 등록하는 공지사항 (중요 공지 구분 가능)
CREATE TABLE notices (
    id        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 공지사항 고유 ID
    title     VARCHAR(255) NOT NULL,                            -- 제목
    content   TEXT NOT NULL,                                    -- 내용
    important BOOLEAN NOT NULL DEFAULT FALSE,                   -- 중요 공지 여부 (true면 상단 표시)
    created   TIMESTAMP DEFAULT CURRENT_TIMESTAMP               -- 등록일시
);


-- ===== QnA 테이블 =====
-- 관리자가 등록하는 자주 묻는 질문 (카테고리별 분류)
CREATE TABLE qnas (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- QnA 고유 ID
    category VARCHAR(255) NOT NULL,                            -- 카테고리 (주문/결제, 배송, 교환/반품 등)
    question VARCHAR(255) NOT NULL,                            -- 질문
    answer   TEXT NOT NULL,                                    -- 답변
    created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP               -- 등록일시
);


-- ===== 고객문의 테이블 =====
-- 일반회원이 등록하는 1:1 문의 (관리자 답변 포함)
CREATE TABLE inquiries (
    id         INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 문의 고유 ID
    type       VARCHAR(255) NOT NULL,                            -- 문의 유형 (주문/결제, 배송/교환/반품 등)
    title      VARCHAR(255) NOT NULL,                            -- 문의 제목
    content    TEXT NOT NULL,                                    -- 문의 내용
    login_id   VARCHAR(50) NOT NULL,                             -- 작성자 로그인 아이디
    name       VARCHAR(50) NOT NULL,                             -- 작성자 이름
    status     VARCHAR(50) NOT NULL,                             -- 상태 (답변대기, 답변완료)
    reply      TEXT,                                             -- 관리자 답변 내용
    replied_at TIMESTAMP,                                        -- 답변 등록일시
    created    TIMESTAMP DEFAULT CURRENT_TIMESTAMP               -- 문의 등록일시
);


-- ===== 커뮤니티 게시판 테이블 =====
-- 일반회원이 자유롭게 작성하는 커뮤니티 게시글
CREATE TABLE boards (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 게시글 고유 ID
    title    VARCHAR(255) NOT NULL,                            -- 제목
    content  TEXT NOT NULL,                                    -- 내용
    login_id VARCHAR(50) NOT NULL,                             -- 작성자 로그인 아이디
    name     VARCHAR(50) NOT NULL,                             -- 작성자 이름
    created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP               -- 작성일시
);


-- ===== 댓글 테이블 =====
-- 커뮤니티 게시글에 달리는 댓글 (boards 테이블과 1:N 관계)
CREATE TABLE comments (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 댓글 고유 ID
    content  TEXT NOT NULL,                                    -- 댓글 내용
    login_id VARCHAR(50) NOT NULL,                             -- 작성자 로그인 아이디
    name     VARCHAR(50) NOT NULL,                             -- 작성자 이름
    board_id INTEGER NOT NULL,                                 -- 게시글 ID (boards.id 참조)
    created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP               -- 작성일시
);


-- ===== 리뷰 테이블 =====
-- 배송완료된 상품에 대해 구매자가 작성하는 리뷰
-- 동일 상품에 대한 중복 리뷰 방지 (서비스 레이어에서 처리)
CREATE TABLE reviews (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,        -- 리뷰 고유 ID
    item_id  INTEGER NOT NULL,                                        -- 상품 ID (items.id 참조)
    login_id VARCHAR(50) NOT NULL,                                    -- 작성자 로그인 아이디
    name     VARCHAR(50) NOT NULL,                                    -- 작성자 이름
    content  TEXT NOT NULL,                                           -- 리뷰 내용
    rating   INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),   -- 별점 (1~5점)
    created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP                      -- 작성일시
);