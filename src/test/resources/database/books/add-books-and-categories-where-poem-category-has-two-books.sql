INSERT INTO categories (id, name, is_deleted) VALUES (1, 'poem', false);
INSERT INTO categories (id, name, is_deleted) VALUES (2, 'fantasy', false);

INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES (1, 'Kobzar', 'Taras Shevchenko', '12345678900', 10.25, false);
INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES (2, 'Fantasy book', 'Some Fantasy Author', '12345678901', 11.05, false);
INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES (3, 'Poem-Fantasy book', 'Some Author', '12345678902', 5.5, false);

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (2, 2);
INSERT INTO books_categories (book_id, category_id) VALUES (3, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (3, 2);