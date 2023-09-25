package book.store.service.impl;

import book.store.dto.cart.CartItemCreateRequestDto;
import book.store.dto.cart.CartItemResponseDto;
import book.store.dto.cart.CartItemUpdateRequestDto;
import book.store.mapper.CartItemMapper;
import book.store.model.CartItem;
import book.store.repository.CartItemRepository;
import book.store.repository.ShoppingCartRepository;
import book.store.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public void create(String username, CartItemCreateRequestDto requestDto) {
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setShoppingCart(shoppingCartRepository.findShoppingCartByUserEmail(username));
        cartItemRepository.save(cartItem);
    }

    @Override
    public CartItemResponseDto getById(Long id) {
        return cartItemMapper.toDto(cartItemRepository.getReferenceById(id));
    }

    @Override
    public void update(Long cartItemId, CartItemUpdateRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.getReferenceById(cartItemId);
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }
}
