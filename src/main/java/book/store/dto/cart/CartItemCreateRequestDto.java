package book.store.dto.cart;

import lombok.Data;

@Data
public class CartItemCreateRequestDto {
    private Long bookId;
    private int quantity;
}
