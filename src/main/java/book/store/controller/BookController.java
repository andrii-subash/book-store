package book.store.controller;

import book.store.dto.BookRequestDto;
import book.store.dto.BookResponseDto;
import book.store.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookResponseDto> getAll() {
        return bookService.findAll();
    }

    @PostMapping
    public BookResponseDto createBook(@RequestBody BookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @GetMapping("/{id}")
    private BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }
}
