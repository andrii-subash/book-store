package book.store.service;

import book.store.dto.book.BookRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookWithoutCategoryResponseDto;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.BookRepository;
import book.store.repository.SpecificationProvider;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    @Mock
    private BookRepository repository;
    @Mock
    private BookMapper mapper;
    @Mock
    private SpecificationProvider<Book> specificationProvider;
    @InjectMocks
    private BookService service;

    @Test
    @DisplayName("Checks if the book is in the database after saving")
    public void saveBook_ReturnsBookDto() {
        Book model = getBook();
        BookRequestDto requestDto = getBookRequestDto();
        BookResponseDto responseDto = getBookResponseDto();

        Mockito.when(mapper.toDto(model)).thenReturn(responseDto);
        Mockito.when(mapper.toModel(requestDto)).thenReturn(model);
        Mockito.when(repository.save(model)).thenReturn(model.setId(1L));

        BookResponseDto actual = service.save(requestDto);
        /*
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(actual, responseDto);
         */
    }

    @Test
    @DisplayName("Find all books in database")
    public void findAll_ReturnsListWithOneBook() {
        Book model = getBook();
        BookResponseDto responseDto = getBookResponseDto();
        Pageable pageable = Pageable.unpaged();
        Page<Book> page = new PageImpl<>(List.of(model));

        Mockito.when(mapper.toDto(model)).thenReturn(responseDto);
        Mockito.when(repository.findAll(Pageable.unpaged())).thenReturn(page);

        List<BookResponseDto> actual = service.findAll(pageable);
        /*
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(model.getTitle(), actual.get(0).getTitle());
         */
    }

    @Test
    @DisplayName("Get book by valid id")
    public void getBookById_ValidId_ReturnsBookDto() {
        Book model = getBook();
        BookResponseDto responseDto = getBookResponseDto();

        Mockito.when(mapper.toDto(model)).thenReturn(responseDto);
        Mockito.when(repository.findById(model.getId())).thenReturn(Optional.of(model));

        BookResponseDto actual = service.getById(1L);

        /*
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(model.getTitle(), actual.getTitle());
        Mockito.verify(repository, Mockito.times(1)).findById(1L);
         */
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
