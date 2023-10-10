package book.store.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import book.store.dto.cart.CartItemResponseDto;
import book.store.dto.cart.ShoppingCartResponseDto;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.ShoppingCartRepository;
import book.store.service.impl.ShoppingCartServiceImpl;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository repository;
    @Mock
    private ShoppingCartMapper mapper;
    @InjectMocks
    private ShoppingCartServiceImpl service;
    
    @Test
    @DisplayName("Get shopping cart by user email")
    public void getShoppingCartByUserEmail_ValidEmail_ReturnsShoppingCartDto() {
        String email = "user123@email.com";
        ShoppingCart model = getShoppingCart();
        ShoppingCartResponseDto responseDto = getShoppingCartResponseDto();
        
        when(repository.findShoppingCartByUserEmail(email)).thenReturn(model);
        when(mapper.toDto(model)).thenReturn(responseDto);
        ShoppingCartResponseDto actual = service.getShoppingCartByUserEmail(email);
        
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }
    
    private ShoppingCart getShoppingCart() {
        return new ShoppingCart()
                       .setId(1L)
                       .setUser(new User().setId(1L))
                       .setCartItems(Set.of(
                               new CartItem().setId(1L),
                               new CartItem().setId(2L)
                       ));
    }
    
    private ShoppingCartResponseDto getShoppingCartResponseDto() {
        return new ShoppingCartResponseDto()
                       .setId(1L)
                       .setUserId(1L)
                       .setCartItems(Set.of(
                               new CartItemResponseDto().setId(1L),
                               new CartItemResponseDto().setId(2L)
                       ));
    }
}
