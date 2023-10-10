package book.store.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemUpdateRequestDto {
    @NotNull
    @Min(value = 0)
    private Integer quantity;
}
