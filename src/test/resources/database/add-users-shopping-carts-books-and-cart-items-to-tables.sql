INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted) VALUES (1, 'user123@email.com', 'password', 'Bob', 'Bob`s lastname', 'First Shipping Address', false);
INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted) VALUES (2, 'user234@email.com', 'password', 'Alice', 'Alice`s lastname', 'Second Shipping Address', false);
INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted) VALUES (3, 'admin@email.com', 'password', 'Admin', 'Admin`s lastname', 'Third Shipping Address', false);

INSERT INTO shopping_carts (id, user_id, is_deleted) VALUES (1, 1, false);
INSERT INTO shopping_carts (id, user_id, is_deleted) VALUES (2, 2, false);

INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES (1, 'First Book', 'First Author', '12345678900', 10.25, false);
INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES (2, 'Second Book', 'Second Author', '12345678901', 11.05, false);
INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES (3, 'Third Book', 'Third Author', '12345678902', 5.5, false);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity) VALUES (1, 1, 1, 5);
