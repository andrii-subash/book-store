package book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import book.store.dto.book.BookRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.BookWithoutCategoryResponseDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.BookRepository;
import book.store.repository.SpecificationProvider;
import book.store.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceImplTest {
    @Mock
    private BookRepository repository;
    @Mock
    private BookMapper mapper;
    @Mock
    private SpecificationProvider<Book> specificationProvider;
    @InjectMocks
    private BookServiceImpl service;

    @Test
    @DisplayName("Checks if the book is in the database after saving")
    public void saveBook_ReturnsBookDto() {
        Book model = getBook();
        BookRequestDto requestDto = getBookRequestDto();
        BookResponseDto responseDto = getBookResponseDto();

        when(mapper.toDto(model)).thenReturn(responseDto);
        when(mapper.toModel(requestDto)).thenReturn(model);
        when(repository.save(model)).thenReturn(model.setId(1L));
        BookResponseDto actual = service.save(requestDto);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(responseDto, actual);
    }

    @Test
    @DisplayName("Find all books in database")
    public void findAll_ReturnsListWithOneBookDto() {
        Book model = getBook();
        BookResponseDto responseDto = getBookResponseDto();
        Pageable pageable = Pageable.unpaged();
        Page<Book> page = new PageImpl<>(List.of(model));

        when(mapper.toDto(model)).thenReturn(responseDto);
        when(repository.findAll(Pageable.unpaged())).thenReturn(page);
        List<BookResponseDto> actual = service.findAll(pageable);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(responseDto.getTitle(), actual.get(0).getTitle());
    }

    @Test
    @DisplayName("Get book by valid id")
    public void getBookById_ValidId_ReturnsBookDto() {
        Book model = getBook();
        BookResponseDto responseDto = getBookResponseDto();

        when(mapper.toDto(model)).thenReturn(responseDto);
        when(repository.getReferenceById(1L)).thenReturn(model);
        BookResponseDto actual = service.getById(1L);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }

    @Test
    @DisplayName("Get exception after searching book by not valid id")
    public void getBookById_NotValidId_ReturnsException() {
        Long id = -10L;

        when(repository.getReferenceById(id)).thenThrow(new RuntimeException("error message"));

        Exception exception = assertThrows(RuntimeException.class, () -> service.getById(id));
        assertEquals("error message", exception.getMessage());
    }

    @Test
    @DisplayName("Update book with valid id and dto")
    public void updateBook_ValidIdAndDto_ReturnsUpdatedBookDto() {
        Long existedId = 1L;
        Book modelFromDb = getBook().setId(existedId);
        Book newModel = getBook().setTitle("Other Title");
        BookRequestDto requestDto = getBookRequestDto();
        BookResponseDto responseDto = getBookResponseDto().setTitle(newModel.getTitle());

        when(repository.findById(existedId)).thenReturn(Optional.of(modelFromDb));
        when(mapper.toModel(requestDto)).thenReturn(newModel);
        when(repository.save(newModel)).thenReturn(newModel.setId(existedId));
        when(mapper.toDto(newModel)).thenReturn(responseDto);
        BookResponseDto actual = service.update(existedId, requestDto);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }

    @Test
    @DisplayName("Update book with valid dto and not valid id")
    public void updateBook_ValidDtoAndNotValidId_ReturnsException() {
        Long notExistedId = -1L;
        BookRequestDto requestDto = getBookRequestDto();

        when(repository.findById(notExistedId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> service.update(notExistedId, requestDto));
        assertEquals("The book with id " + notExistedId + " does not exist",
                exception.getMessage());
    }

    @Test
    @DisplayName("Find books by searching parameters")
    public void searchBooks_ValidParameters_ReturnsListWithOneBookDto() {
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto(
                Collections.emptyList(), List.of("Author"));
        Book model = getBook();
        BookResponseDto responseDto = getBookResponseDto();
        Specification<Book> specification = null;

        when(specificationProvider.getSpecification(anyList(), anyString())).thenReturn(any());
        when(repository.findAll(specification)).thenReturn(List.of(model));
        when(mapper.toDto(model)).thenReturn(responseDto);
        List<BookResponseDto> actual = service.searchBooks(searchParametersDto);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual.get(0));
    }

    @Test
    @DisplayName("Find books by category ids")
    public void findBooksByCategoryId_ValidCategoryId_ReturnsListWithOneBookDto() {
        Long existedCategoryId = 2L;
        Book model = getBook();
        BookWithoutCategoryResponseDto responseDto = getBookWithoutCategoryIdsResponseDto();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAllBooksByCategoryId(existedCategoryId, pageable))
                .thenReturn(List.of(model));
        when(mapper.toDtoWithoutCategories(model)).thenReturn(responseDto);
        List<BookWithoutCategoryResponseDto> actual =
                service.findAllByCategoryId(existedCategoryId, pageable);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual.get(0));
    }

    private BookRequestDto getBookRequestDto() {
        return new BookRequestDto()
                .setAuthor("Author")
                .setIsbn("0123456789")
                .setTitle("Title")
                .setDescription("Description")
                .setPrice(25.55)
                .setCoverImage("image.jpg")
                .setCategoryIds(Set.of(1L, 2L));
    }

    private BookResponseDto getBookResponseDto() {
        return new BookResponseDto()
                .setId(1L)
                .setAuthor("Author")
                .setIsbn("0123456789")
                .setTitle("Title")
                .setDescription("Description")
                .setPrice(25.55)
                .setCoverImage("image.jpg")
                .setCategoryIds(Set.of(1L, 2L));
    }

    private BookWithoutCategoryResponseDto getBookWithoutCategoryIdsResponseDto() {
        return new BookWithoutCategoryResponseDto()
                .setId(1L)
                .setAuthor("Author")
                .setIsbn("0123456789")
                .setTitle("Title")
                .setDescription("Description")
                .setPrice(25.55)
                .setCoverImage("image.jpg");
    }

    private Book getBook() {
        return new Book()
                .setAuthor("Author")
                .setIsbn("0123456789")
                .setTitle("Title")
                .setDescription("Description")
                .setPrice(BigDecimal.valueOf(25.55))
                .setCoverImage("image.jpg")
                .setCategories(Set.of(
                        new Category().setId(1L),
                        new Category().setId(2L)
                ));
    }
}
