CREATE INDEX idx_product_category_id
    ON product (category_id);

CREATE INDEX idx_orders_user_id
    ON orders (user_id);

CREATE INDEX idx_stock_location_id
    ON stock (location_id);

CREATE INDEX idx_order_detail_product_id
    ON order_detail (product_id);

CREATE INDEX idx_order_detail_shipped_from
    ON order_detail (shipped_from);
