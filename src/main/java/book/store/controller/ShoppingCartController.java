package book.store.controller;

import book.store.dto.cart.CartItemCreateRequestDto;
import book.store.dto.cart.CartItemUpdateRequestDto;
import book.store.dto.cart.ShoppingCartResponseDto;
import book.store.service.CartItemService;
import book.store.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
@PreAuthorize("hasRole('ROLE_USER')")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @GetMapping
    @Operation(summary = "Get a shopping cart")
    public ShoppingCartResponseDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCartByUserEmail(authentication.getName());
    }

    @PostMapping
    @Operation(summary = "Add a book to shopping cart")
    public ShoppingCartResponseDto addBookToShoppingCart(
            Authentication authentication,
            @RequestBody @Valid CartItemCreateRequestDto requestDto) {
        cartItemService.create(authentication.getName(), requestDto);
        return shoppingCartService.getShoppingCartByUserEmail(authentication.getName());
    }

    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update books quantity in shopping cart")
    public ShoppingCartResponseDto updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody @Valid CartItemUpdateRequestDto requestDto,
            Authentication authentication) {
        cartItemService.update(cartItemId, requestDto);
        return shoppingCartService.getShoppingCartByUserEmail(authentication.getName());
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Delete a book from shopping cart")
    public ShoppingCartResponseDto deleteCartItem(@PathVariable Long cartItemId,
                                                  Authentication authentication) {
        cartItemService.deleteById(cartItemId);
        return shoppingCartService.getShoppingCartByUserEmail(authentication.getName());
    }

}
