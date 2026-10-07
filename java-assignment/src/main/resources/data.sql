INSERT INTO store(id, name, quantity_products_in_stock) VALUES (1, 'TONSTAD', 10);
INSERT INTO store(id, name, quantity_products_in_stock) VALUES (2, 'KALLAX', 5);
INSERT INTO store(id, name, quantity_products_in_stock) VALUES (3, 'BESTÅ', 3);
ALTER TABLE store ALTER COLUMN id RESTART WITH 4;

INSERT INTO product(id, name, stock) VALUES (1, 'TONSTAD', 10);
INSERT INTO product(id, name, stock) VALUES (2, 'KALLAX', 5);
INSERT INTO product(id, name, stock) VALUES (3, 'BESTÅ', 3);
ALTER TABLE product ALTER COLUMN id RESTART WITH 4;

INSERT INTO warehouse(id, business_unit_code, location, capacity, stock, created_at, archived_at)
VALUES (1, 'MWH.001', 'ZWOLLE-001', 100, 10, '2024-07-01', null);
INSERT INTO warehouse(id, business_unit_code, location, capacity, stock, created_at, archived_at)
VALUES (2, 'MWH.012', 'AMSTERDAM-001', 50, 5, '2023-07-01', null);
INSERT INTO warehouse(id, business_unit_code, location, capacity, stock, created_at, archived_at)
VALUES (3, 'MWH.023', 'TILBURG-001', 30, 27, '2021-02-01', null);
ALTER TABLE warehouse ALTER COLUMN id RESTART WITH 4;
