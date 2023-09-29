package book.store.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemCreateRequestDto {
    @NotNull
    private Long bookId;
    @NotNull
    @Min(value = 0)
    private int quantity;
}
