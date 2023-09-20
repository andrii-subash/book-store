package book.store.controller;

import book.store.dto.BookRequestDto;
import book.store.dto.BookResponseDto;
import book.store.dto.BookSearchParametersDto;
import book.store.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all available books")
    public List<BookResponseDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books by title or author")
    private List<BookResponseDto> searchBooks(BookSearchParametersDto bookSearchParametersDto) {
        return bookService.searchBooks(bookSearchParametersDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID")
    private BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new book")
    public BookResponseDto createBook(@RequestBody @Valid BookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update information for book")
    private BookResponseDto update(@PathVariable Long id,
                                   @RequestBody @Valid BookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    private void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
