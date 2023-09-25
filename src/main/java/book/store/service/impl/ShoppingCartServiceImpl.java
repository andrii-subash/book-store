package book.store.service.impl;

import book.store.dto.cart.ShoppingCartResponseDto;
import book.store.mapper.ShoppingCartMapper;
import book.store.repository.ShoppingCartRepository;
import book.store.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartResponseDto getShoppingCartByUserEmail(String username) {
        return shoppingCartMapper.toDto(shoppingCartRepository
                .findShoppingCartByUserEmail(username));
    }
}
