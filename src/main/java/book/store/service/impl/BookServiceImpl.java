package book.store.service.impl;

import book.store.dto.book.BookRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.BookWithoutCategoryResponseDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.BookRepository;
import book.store.repository.SpecificationProvider;
import book.store.service.BookService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final SpecificationProvider<Book> specificationProvider;

    @Override
    public BookResponseDto save(BookRequestDto requestDto) {
        Book book = bookRepository.save(bookMapper.toModel(requestDto));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookResponseDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDto getById(Long id) {
        return bookMapper.toDto(bookRepository.getReferenceById(id));
    }

    @Override
    public BookResponseDto update(Long id, BookRequestDto requestDto) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("The book with id " + id + " does not exist");
        }
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookResponseDto> searchBooks(BookSearchParametersDto bookSearchParametersDto) {
        Map<String, List<String>> params = bookSearchParametersDto.getParams();

        Specification<Book> specification = null;
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                Specification<Book> sp = specificationProvider
                        .getSpecification(entry.getValue(), entry.getKey());
                specification = specification == null
                        ? Specification.where(sp) : specification.and(sp);
            }
        }
        return bookRepository.findAll(specification).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookWithoutCategoryResponseDto> findAllByCategoryId(Long categoryId,
                                                                    Pageable pageable) {
        return bookRepository.findAllBooksByCategoryId(categoryId, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
