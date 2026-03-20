--상품 테이블 생성
CREATE TABLE items (
    id      		Integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 	--자동증가 식별자
    name    		VARCHAR(255) NOT NULL, 							  	--상품이름 (빈칸에 입력불가,필수입력)
    image_path      VARCHAR(500), 										--상품 이미지 경로 (선택입력)
    price           NUMERIC(10, 2) NOT NULL, --가격 (소수점 2자리, 필수입력)
    discount_rate   NUMERIC(5, 2) DEFAULT 0 CHECK (discount_rate >= 0 AND discount_rate <= 100), --할인율0~100% (기본값0) 
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP 				--상품등록일시 (자동입력)
);

--회원가입 테이블 생성
CREATE TABLE members (
    id              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 자동증가 식별자
    name            VARCHAR(50) NOT NULL,                             -- 회원이름 (필수입력)
    login_id        VARCHAR(50) NOT NULL UNIQUE,                      -- 로그인 아이디 (중복불가)
    login_pw        VARCHAR(255) NOT NULL,                            -- 로그인 비밀번호 (암호화 저장 필수)
    email           VARCHAR(100) UNIQUE,                              -- 이메일 (중복불가)
    phone           VARCHAR(20),                                      -- 휴대폰번호 (ex 010-1234-5678)
    address         VARCHAR(255),                                     -- 기본주소 (ex 서울시 강남구 청담동)
    address_detail  VARCHAR(255),                                     -- 상세주소 (ex 101동 202호)
    zipcode         VARCHAR(10),                                      -- 우편번호
    created         TIMESTAMP DEFAULT CURRENT_TIMESTAMP               -- 가입일시 (자동입력)
);

	--장바구니 테이블 생성
	CREATE TABLE carts (

	id 				SERIAL PRIMARY KEY, 			   --장바구니 고유아이디
	member_id 		INTEGER NOT NULL,			   	   --회원 아이디
	product_id 		INTEGER NOT NULL,				   --상품 아이디
	quantity		INT NOT NULL DEFAULT 1,			   --수량
	created_at 		TIMESTAMP DEFAULT NOW()			   --생성 일시
	);

	-- 주문 테이블 생성
	CREATE TABLE orders (
    id          	SERIAL PRIMARY KEY,         -- 주문 고유 아이디
    member_id   	INTEGER NOT NULL,           -- 주문 회원 아이디
    name       		VARCHAR(50) NOT NULL,       -- 주문자 이름
    address     	VARCHAR(500) NOT NULL,      -- 배송 주소
    payment     	VARCHAR(10) NOT NULL,       -- 결제 수단
    card_number 	VARCHAR(16),                -- 카드 번호 (카드결제 시)
    amount      	INTEGER NOT NULL,           -- 최종 결제 금액
    created_at  	TIMESTAMP DEFAULT NOW()     -- 생성 일시
);

	-- 주문 상품 테이블 생성
	CREATE TABLE order_items (
    id          	SERIAL PRIMARY KEY,         -- 주문 상품 고유 아이디
    order_id    	INTEGER NOT NULL,           -- 주문 아이디
    item_id     	INTEGER NOT NULL,           -- 상품 아이디
    quantity    	INTEGER NOT NULL DEFAULT 1, -- 주문 수량
    created_at  	TIMESTAMP DEFAULT NOW()     -- 생성 일시
);


	--테이블 데이터 입력
	INSERT INTO items (name, image_path, price, discount_rate) --id, created_at 은 자동입력
	VALUES 
			--상품명, 이미지경로, 가격, 할인율--
    ('Starry', '/image/001.jpg', 129000, 10.00), --할인율10% 
    ('Seascape', '/image/002.jpg', 89000, 15.00), --할인율15%
    ('Arles', '/image/003.jpg', 35000, 0.00), --할인없음
    ('Mountain', '/image/004.jpg', 49000, 20.00), --할인율20%
    ('Provence', '/image/005.jpg', 28000, 5.00), --할인율5%
	('Houses', '/image/006.jpg', 28000, 30.00); --할인율30%

	--회원 정보 입력
	INSERT INTO members(name, login_id,login_pw,email,phone,address,address_detail,zipcode)
	VALUES
		('홍길동', 'hong123', '1234', 'hong@gmail.com', '010-1234-5678', '서울시 강남구 청담동 장미아파트', '101동 202호', '06234');
	
	--테이블 확인
	select * from items;

	--테이블명 수정
	ALTER TABLE products RENAME TO items;

	--테이블 삭제
	drop table carts; 


	