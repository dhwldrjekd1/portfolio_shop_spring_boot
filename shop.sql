-- ===== 상품 테이블 =====
CREATE TABLE items (
    id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    image_path    VARCHAR(500),
    price         DOUBLE PRECISION NOT NULL,
    discount_rate DOUBLE PRECISION DEFAULT 0,
    stock         INTEGER NOT NULL DEFAULT 0,
    description   VARCHAR(255),
    badge         VARCHAR(50),
    category      VARCHAR(50),
    details_json  TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===== 회원 테이블 =====
CREATE TABLE members (
    id             INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name           VARCHAR(50) NOT NULL,
    login_id       VARCHAR(50) NOT NULL UNIQUE,
    login_pw       VARCHAR(255) NOT NULL,
    email          VARCHAR(100) UNIQUE,
    phone          VARCHAR(20),
    address        VARCHAR(255),
    address_detail VARCHAR(255),
    zipcode        VARCHAR(10),
    role           VARCHAR(20) DEFAULT 'user',
    grade          VARCHAR(20) DEFAULT '브론즈',
    created        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===== 장바구니 테이블 =====
CREATE TABLE carts (
    id       SERIAL PRIMARY KEY,
    login_id VARCHAR(255) NOT NULL,
    item_id  INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    color    VARCHAR(50),
    size     VARCHAR(50),
    created  TIMESTAMP NOT NULL
);

-- ===== 주문 테이블 =====
CREATE TABLE orders (
    id          SERIAL PRIMARY KEY,
    login_id    VARCHAR(255) NOT NULL,
    name        VARCHAR(50) NOT NULL,
    address     VARCHAR(500) NOT NULL,
    payment     VARCHAR(10) NOT NULL,
    card_number VARCHAR(16),
    amount      INTEGER NOT NULL,
    status      VARCHAR(50) NOT NULL DEFAULT '주문접수',
    created     TIMESTAMP NOT NULL
);

-- ===== 주문 상품 테이블 =====
CREATE TABLE order_items (
    id        SERIAL PRIMARY KEY,
    item_id   INTEGER NOT NULL,
    quantity  INTEGER NOT NULL DEFAULT 1,
    item_name VARCHAR(255),
    color     VARCHAR(50),
    size      VARCHAR(50),
    order_id  INTEGER NOT NULL
);

-- ===== 공지사항 테이블 =====
CREATE TABLE notices (
    id        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    content   TEXT NOT NULL,
    important BOOLEAN NOT NULL DEFAULT FALSE,
    created   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===== QnA 테이블 =====
CREATE TABLE qnas (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    question VARCHAR(255) NOT NULL,
    answer   TEXT NOT NULL,
    created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===== 고객문의 테이블 =====
CREATE TABLE inquiries (
    id         INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type       VARCHAR(255) NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT NOT NULL,
    login_id   VARCHAR(50) NOT NULL,
    name       VARCHAR(50) NOT NULL,
    status     VARCHAR(50) NOT NULL,
    reply      TEXT,
    replied_at TIMESTAMP,
    created    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===== 커뮤니티 게시판 테이블 =====
CREATE TABLE boards (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title    VARCHAR(255) NOT NULL,
    content  TEXT NOT NULL,
    login_id VARCHAR(50) NOT NULL,
    name     VARCHAR(50) NOT NULL,
    created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===== 댓글 테이블 =====
CREATE TABLE comments (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content  TEXT NOT NULL,
    login_id VARCHAR(50) NOT NULL,
    name     VARCHAR(50) NOT NULL,
    board_id INTEGER NOT NULL,
    created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===== 리뷰 테이블 =====
CREATE TABLE reviews (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id  INTEGER NOT NULL,
    login_id VARCHAR(50) NOT NULL,
    name     VARCHAR(50) NOT NULL,
    content  TEXT NOT NULL,
    rating   INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);