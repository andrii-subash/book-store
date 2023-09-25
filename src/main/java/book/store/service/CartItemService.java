package book.store.service;

import book.store.dto.cart.CartItemCreateRequestDto;
import book.store.dto.cart.CartItemResponseDto;
import book.store.dto.cart.CartItemUpdateRequestDto;

public interface CartItemService {
    void create(String username, CartItemCreateRequestDto requestDto);

    CartItemResponseDto getById(Long id);

    void update(Long cartItemId, CartItemUpdateRequestDto requestDto);

    void deleteById(Long id);
}
