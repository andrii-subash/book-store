INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted) VALUES (1, 'user123@email.com', 'password', 'Bob', 'Bob`s lastname', 'First Shipping Address', false);
INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted) VALUES (2, 'user234@email.com', 'password', 'Alice', 'Alice`s lastname', 'Second Shipping Address', false);
INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted) VALUES (3, 'admin@email.com', 'password', 'Admin', 'Admin`s lastname', 'Third Shipping Address', false);

INSERT INTO shopping_carts (id, user_id, is_deleted) VALUES (1, 1, false);
INSERT INTO shopping_carts (id, user_id, is_deleted) VALUES (2, 2, false);
