package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.cart.CartItemCreateRequestDto;
import book.store.dto.cart.CartItemResponseDto;
import book.store.model.Book;
import book.store.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemResponseDto toDto(CartItem cartItem);

    CartItem toModel(CartItemCreateRequestDto cartItemCreateRequestDto);

    @AfterMapping
    default void setBookForCreating(@MappingTarget CartItem cartItem,
                                    CartItemCreateRequestDto cartItemCreateRequestDto) {
        Book book = new Book();
        book.setId(cartItemCreateRequestDto.getBookId());
        cartItem.setBook(book);
    }
}
