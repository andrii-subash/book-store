package book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import book.store.dto.cart.CartItemCreateRequestDto;
import book.store.dto.cart.CartItemResponseDto;
import book.store.dto.cart.CartItemUpdateRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.BookRepository;
import book.store.repository.CartItemRepository;
import book.store.repository.ShoppingCartRepository;
import book.store.service.impl.CartItemServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@RunWith(MockitoJUnitRunner.class)
public class CartItemServiceImplTest {
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private CartItemServiceImpl cartItemService;
    
    @Test
    @DisplayName("Instead of creating, it will update cart item quantity "
                         + "if book is present in shopping cart")
    public void createCartItem_BookIsPresentInShoppingCart_UpdateBookQuantity() {
        String email = "user123@email.com";
        CartItem model = getCartItem().setId(1L);
        ShoppingCart shoppingCart = new ShoppingCart()
                                            .setId(1L)
                                            .setUser(new User()
                                                             .setId(1L)
                                                             .setEmail(email))
                                            .setCartItems(Set.of(model));
        CartItemCreateRequestDto requestDto = getCartItemCreateRequestDto();
        
        when(shoppingCartRepository.findShoppingCartByUserEmail(email)).thenReturn(shoppingCart);
        when(cartItemRepository.save(model)).thenReturn(model);
        cartItemService.create(email, requestDto);
        
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserEmail(email);
        verify(cartItemRepository, times(1)).save(model);
        assertEquals(6, model.getQuantity());
    }
    
    @Test
    @DisplayName("Create cart item if book is not present in shopping cart")
    public void createCartItem_BookIsNotPresentInShoppingCart_CreateCartItem() {
        String email = "user123@email.com";
        CartItem model = getCartItem();
        ShoppingCart shoppingCart = new ShoppingCart()
                                            .setId(1L)
                                            .setUser(new User()
                                                             .setId(1L)
                                                             .setEmail(email))
                                            .setCartItems(new HashSet<>());
        CartItemCreateRequestDto requestDto = getCartItemCreateRequestDto();
        
        when(shoppingCartRepository.findShoppingCartByUserEmail(email)).thenReturn(shoppingCart);
        when(cartItemMapper.toModel(requestDto)).thenReturn(model);
        when(bookRepository.getReferenceById(model.getBook().getId())).thenReturn(model.getBook());
        when(cartItemRepository.save(model)).thenReturn(model.setId(1L));
        cartItemService.create(email, requestDto);
        
        verify(shoppingCartRepository, times(2)).findShoppingCartByUserEmail(email);
        verify(cartItemRepository, times(1)).save(model);
        assertTrue(shoppingCart.getCartItems().contains(model));
    }
    
    @Test
    @DisplayName("Get cart item by valid id")
    public void getCartItemById_ValidId_ReturnsCartItemDto() {
        CartItem model = getCartItem();
        CartItemResponseDto responseDto = getCartItemResponseDto();
        
        when(cartItemMapper.toDto(model)).thenReturn(responseDto);
        when(cartItemRepository.getReferenceById(1L)).thenReturn(model);
        CartItemResponseDto actual = cartItemService.getById(1L);
        
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }
    
    @Test
    @DisplayName("Get exception after searching cart item by not valid id")
    public void getCartItemById_NotValidId_ReturnsException() {
        Long id = -10L;
        
        when(cartItemRepository.getReferenceById(id))
                .thenThrow(new RuntimeException("error message"));
        
        Exception exception = assertThrows(RuntimeException.class,
                () -> cartItemService.getById(id));
        assertEquals("error message", exception.getMessage());
    }
    
    @Test
    @DisplayName("Update cart item with valid id and dto")
    public void updateCartItem_ValidIdAndDto_ReturnsUpdatedCartItemDto() {
        Long existedId = 1L;
        CartItem modelFromDb = getCartItem().setId(existedId);
        CartItemUpdateRequestDto requestDto = getCartItemUpdateRequestDto();
        
        when(cartItemRepository.findById(existedId)).thenReturn(Optional.of(modelFromDb));
        when(cartItemRepository.save(modelFromDb)).thenReturn(modelFromDb);
        cartItemService.update(existedId, requestDto);
        
        verify(cartItemRepository, times(1)).findById(existedId);
        verify(cartItemRepository, times(1)).save(modelFromDb);
        assertEquals(requestDto.getQuantity(), modelFromDb.getQuantity());
    }
    
    @Test
    @DisplayName("Instead of updating, it will delete cart item if book quantity is zero")
    public void updateCartItem_BookQuantityIsZero_ReturnsShoppingCartWithoutThisDto() {
        Long existedId = 1L;
        CartItem modelFromDb = getCartItem().setId(existedId);
        CartItemUpdateRequestDto requestDto = getCartItemUpdateRequestDto().setQuantity(0);
        
        when(cartItemRepository.findById(existedId)).thenReturn(Optional.of(modelFromDb));
        cartItemService.update(existedId, requestDto);
        
        verify(cartItemRepository, times(1)).findById(existedId);
        verify(cartItemRepository, times(1)).deleteById(existedId);
        verify(cartItemRepository, times(0)).save(modelFromDb);
    }
    
    @Test
    @DisplayName("Update cart item with valid dto and not valid id")
    public void updateCartItem_ValidDtoAndNotValidId_ReturnsException() {
        Long notExistedId = -1L;
        CartItemUpdateRequestDto requestDto = getCartItemUpdateRequestDto();
        
        when(cartItemRepository.findById(notExistedId)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.update(notExistedId, requestDto));
        assertEquals("Can`t find cart item with id " + notExistedId, exception.getMessage());
    }
    
    private CartItem getCartItem() {
        return new CartItem()
                       .setBook(new Book()
                                        .setId(1L)
                                        .setTitle("Title")
                                        .setPrice(BigDecimal.TEN))
                       .setQuantity(3)
                       .setShoppingCart(new ShoppingCart()
                                                .setId(1L));
    }
    
    private CartItemResponseDto getCartItemResponseDto() {
        return new CartItemResponseDto()
                       .setId(1L)
                       .setBookTitle("Title")
                       .setBookId(1L)
                       .setQuantity(3);
    }
    
    private CartItemCreateRequestDto getCartItemCreateRequestDto() {
        return new CartItemCreateRequestDto()
                       .setBookId(1L)
                       .setQuantity(3);
    }
    
    private CartItemUpdateRequestDto getCartItemUpdateRequestDto() {
        return new CartItemUpdateRequestDto()
                       .setQuantity(5);
    }
}
