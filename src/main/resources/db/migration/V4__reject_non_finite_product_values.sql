-- PostgreSQL permits NaN and, for floating-point columns, infinity.
-- Existing price >= 0 and weight > 0 constraints do not reject every special value.
ALTER TABLE online_shop.product
    ADD CONSTRAINT chk_product_price_finite CHECK (price <> 'NaN'::numeric),
    ADD CONSTRAINT chk_product_weight_finite CHECK (weight < 'Infinity'::double precision);
