package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.BookRequestDto;
import book.store.dto.BookResponseDto;
import book.store.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookResponseDto toDto(Book book);

    Book toModel(BookRequestDto requestDto);
}
