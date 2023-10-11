package book.store.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemCreateRequestDto {
    @NotNull
    private Long bookId;
    @NotNull
    @Min(value = 1)
    private Integer quantity;
}
