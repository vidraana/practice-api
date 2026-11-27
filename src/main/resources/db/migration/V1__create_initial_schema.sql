CREATE TABLE product_category (
                                  id UUID PRIMARY KEY,
                                  name VARCHAR(100) NOT NULL,
                                  description VARCHAR(500)
);

CREATE TABLE location (
                          id UUID PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          country VARCHAR(100) NOT NULL,
                          city VARCHAR(100) NOT NULL,
                          county VARCHAR(100),
                          street_address VARCHAR(255) NOT NULL
);

CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email_address VARCHAR(255) NOT NULL UNIQUE,
                       role VARCHAR(20) NOT NULL,

                       CONSTRAINT chk_user_role
                           CHECK (role IN ('CUSTOMER', 'ADMIN'))
);

CREATE TABLE product (
                         id UUID PRIMARY KEY,
                         name VARCHAR(150) NOT NULL,
                         description VARCHAR(1000),
                         price NUMERIC(12, 2) NOT NULL,
                         weight DOUBLE PRECISION NOT NULL,
                         category_id UUID NOT NULL,
                         image_url VARCHAR(2048),

                         CONSTRAINT fk_product_category
                             FOREIGN KEY (category_id)
                                 REFERENCES product_category(id),

                         CONSTRAINT chk_product_price
                             CHECK (price >= 0),

                         CONSTRAINT chk_product_weight
                             CHECK (weight > 0)
);

CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        user_id UUID NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        country VARCHAR(100) NOT NULL,
                        city VARCHAR(100) NOT NULL,
                        county VARCHAR(100),
                        street_address VARCHAR(255) NOT NULL,

                        CONSTRAINT fk_order_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id)
);

CREATE TABLE stock (
                       product_id UUID NOT NULL,
                       location_id UUID NOT NULL,
                       quantity INTEGER NOT NULL,

                       CONSTRAINT pk_stock
                           PRIMARY KEY (product_id, location_id),

                       CONSTRAINT fk_stock_product
                           FOREIGN KEY (product_id)
                               REFERENCES product(id),

                       CONSTRAINT fk_stock_location
                           FOREIGN KEY (location_id)
                               REFERENCES location(id),

                       CONSTRAINT chk_stock_quantity
                           CHECK (quantity >= 0)
);

CREATE TABLE order_detail (
                              order_id UUID NOT NULL,
                              product_id UUID NOT NULL,
                              shipped_from UUID NOT NULL,
                              quantity INTEGER NOT NULL,

                              CONSTRAINT pk_order_detail
                                  PRIMARY KEY (order_id, product_id),

                              CONSTRAINT fk_order_detail_order
                                  FOREIGN KEY (order_id)
                                      REFERENCES orders(id),

                              CONSTRAINT fk_order_detail_product
                                  FOREIGN KEY (product_id)
                                      REFERENCES product(id),

                              CONSTRAINT fk_order_detail_location
                                  FOREIGN KEY (shipped_from)
                                      REFERENCES location(id),

                              CONSTRAINT chk_order_detail_quantity
                                  CHECK (quantity > 0)
);
