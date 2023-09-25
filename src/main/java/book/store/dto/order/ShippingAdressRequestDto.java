package book.store.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ShippingAdressRequestDto {
    @NotNull
    @Size(min = 7, max = 40)
    private String shippingAddress;
}
