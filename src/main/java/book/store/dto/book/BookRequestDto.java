package book.store.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String author;
    @NotNull
    @Size(min = 14)
    private String isbn;
    @NotNull
    @Min(value = 0)
    private Long price;
    private String description;
    private String coverImage;
}
