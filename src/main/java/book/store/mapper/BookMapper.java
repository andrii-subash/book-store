package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.book.BookRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookWithoutCategoryResponseDto;
import book.store.model.Book;
import book.store.model.Category;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookResponseDto toDto(Book book);

    Book toModel(BookRequestDto requestDto);

    BookWithoutCategoryResponseDto toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryId(@MappingTarget BookResponseDto bookResponseDto, Book book) {
        bookResponseDto.setCategoryIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
    }

    @AfterMapping
    default void setCategories(@MappingTarget Book book, BookRequestDto bookRequestDto) {
        Set<Category> categorySet = new HashSet<>();
        for (Long categoryId : bookRequestDto.getCategoryIds()) {
            Category category = new Category();
            category.setId(categoryId);
            categorySet.add(category);
        }
        book.setCategories(categorySet);
    }
}
