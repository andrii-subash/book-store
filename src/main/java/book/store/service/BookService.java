package book.store.service;

import book.store.dto.book.BookRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookSearchParametersDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponseDto save(BookRequestDto requestDto);

    List<BookResponseDto> findAll(Pageable pageable);

    BookResponseDto findById(Long id);

    BookResponseDto update(Long id, BookRequestDto requestDto);

    void deleteById(Long id);

    List<BookResponseDto> searchBooks(BookSearchParametersDto bookSearchParametersDto);
}
