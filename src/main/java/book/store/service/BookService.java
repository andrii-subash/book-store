package book.store.service;

import book.store.dto.BookRequestDto;
import book.store.dto.BookResponseDto;
import java.util.List;

public interface BookService {
    BookResponseDto save(BookRequestDto requestDto);

    List<BookResponseDto> findAll();

    BookResponseDto getById(Long id);
}
