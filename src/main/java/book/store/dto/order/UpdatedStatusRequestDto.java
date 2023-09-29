package book.store.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatedStatusRequestDto {
    @NotNull
    private String status;
}
