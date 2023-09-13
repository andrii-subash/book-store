package book.store.dto;

import lombok.Data;

@Data
public class BookRequestDto {
    private String title;
    private String author;
    private String isbn;
    private Long price;
    private String description;
    private String coverImage;
}
