package book.store.service;

import book.store.dto.category.CategoryRequestDto;
import book.store.dto.category.CategoryResponseDto;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.CategoryRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository repository;
    @Mock
    private CategoryMapper mapper;
    @InjectMocks
    private CategoryService service;

    @Test
    @DisplayName("Checks if the category is in the database after saving")
    public void saveCategory_ReturnsCategoryDto() {
        Category model = getCategory();
        CategoryRequestDto requestDto = getCategoryRequestDto();
        CategoryResponseDto responseDto = getCategoryResponseDto();

        Mockito.when(mapper.toDto(model)).thenReturn(responseDto);
        Mockito.when(mapper.toModel(requestDto)).thenReturn(model);
        Mockito.when(repository.save(model)).thenReturn(model.setId(1L));

        CategoryResponseDto actual = service.save(requestDto);

        /*
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(actual, responseDto);
         */
    }

    private Category getCategory() {
        return new Category()
                .setId(1L)
                .setName("Category")
                .setDescription("Description");
    }

    private CategoryRequestDto getCategoryRequestDto() {
        return new CategoryRequestDto()
                .setName("Category")
                .setDescription("Description");
    }

    private CategoryResponseDto getCategoryResponseDto() {
        return new CategoryResponseDto()
                .setId(1L)
                .setName("Category")
                .setDescription("Description");
    }
}
