package book.store.dto.book;

import java.util.Set;
import lombok.Data;

@Data
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Double price;
    private String description;
    private String coverImage;
    private Set<Long> categoryIds;
}
