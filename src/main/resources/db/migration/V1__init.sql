-- Flyway Migration Script for PostgreSQL

CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL
);

CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    category_id INTEGER REFERENCES category(id),
    nome TEXT NOT NULL
);

CREATE INDEX idx_product_nome ON product (category_id, nome);

INSERT INTO category (nome)
SELECT 'Category ' || generate_series(1, 10000);

INSERT INTO product (category_id, nome)
SELECT id, 'Produto ' || id
from generate_series(1, 10000) id;