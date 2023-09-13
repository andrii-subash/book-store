package book.store.dto;

import lombok.Data;

@Data
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Long price;
    private String description;
    private String coverImage;
}
