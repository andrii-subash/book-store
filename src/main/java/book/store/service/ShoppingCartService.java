package book.store.service;

import book.store.dto.cart.ShoppingCartResponseDto;

public interface ShoppingCartService {
    ShoppingCartResponseDto getShoppingCartByUserEmail(String username);
}
