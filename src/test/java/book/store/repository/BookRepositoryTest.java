package book.store.repository;

import book.store.model.Book;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test()
    @DisplayName("Find all books by category id when two books have that category")
    @Sql(scripts = "classpath:database/"
            + "add-books-and-categories-where-poem-category-has-two-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-and-categories-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllBooksByCategoryId_BooksWitCategoryAreExist_ReturnsTwoBooks() {
        List<Book> books = bookRepository.findAllBooksByCategoryId(1L,
                PageRequest.of(0, 10));
        Assertions.assertEquals(2, books.size());
        Assertions.assertEquals("Kobzar", books.get(0).getTitle());
        Assertions.assertEquals("Poem-Fantasy book", books.get(1).getTitle());
    }

    @Test
    @DisplayName("Find all books by category id when no one book has this category")
    @Sql(scripts = "classpath:database/"
            + "add-books-and-categories-where-biography-category-has-no-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-and-categories-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllBooksByCategoryId_BooksWitThatCategoryAreNotExist_ReturnsEmptyList() {
        List<Book> books = bookRepository.findAllBooksByCategoryId(3L,
                PageRequest.of(0, 10));
        Assertions.assertEquals(0, books.size());
    }
}
