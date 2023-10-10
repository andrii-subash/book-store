package book.store.service.impl;

import book.store.dto.cart.CartItemCreateRequestDto;
import book.store.dto.cart.CartItemResponseDto;
import book.store.dto.cart.CartItemUpdateRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.repository.BookRepository;
import book.store.repository.CartItemRepository;
import book.store.repository.ShoppingCartRepository;
import book.store.service.CartItemService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public void create(String username, CartItemCreateRequestDto requestDto) {
        Optional<CartItem> cartItemOptional = isBookPresentInShoppingCart(username, requestDto);
        if (cartItemOptional.isEmpty()) {
            CartItem cartItem = cartItemMapper.toModel(requestDto);
            cartItem.setBook(bookRepository.getReferenceById(cartItem.getBook().getId()));
            ShoppingCart shoppingCart =
                    shoppingCartRepository.findShoppingCartByUserEmail(username);
            cartItem.setShoppingCart(shoppingCart);
            CartItem savedCartItem = cartItemRepository.save(cartItem);
            shoppingCart.getCartItems().add(savedCartItem);
            shoppingCartRepository.save(shoppingCart);
        } else {
            updateQuantityIfBookIsPresentInShoppingCart(cartItemOptional.get(), requestDto);
        }
    }

    @Override
    public CartItemResponseDto getById(Long id) {
        return cartItemMapper.toDto(cartItemRepository.getReferenceById(id));
    }

    @Override
    public void update(Long cartItemId, CartItemUpdateRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find cart item with id " + cartItemId));
        if (requestDto.getQuantity() == 0) {
            cartItemRepository.deleteById(cartItemId);
        } else {
            cartItem.setQuantity(requestDto.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }

    private Optional<CartItem> isBookPresentInShoppingCart(String username,
                                                           CartItemCreateRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserEmail(username);
        return shoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(requestDto.getBookId()))
                .findFirst();
    }

    private void updateQuantityIfBookIsPresentInShoppingCart(CartItem cartItem,
                                                             CartItemCreateRequestDto requestDto) {
        int totalQuantity = cartItem.getQuantity() + requestDto.getQuantity();
        cartItem.setQuantity(totalQuantity);
        cartItemRepository.save(cartItem);
    }
}
