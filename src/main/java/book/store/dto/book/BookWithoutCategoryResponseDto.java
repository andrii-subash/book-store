package book.store.dto.book;

import lombok.Data;

@Data
public class BookWithoutCategoryResponseDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Long price;
    private String description;
    private String coverImage;
}
