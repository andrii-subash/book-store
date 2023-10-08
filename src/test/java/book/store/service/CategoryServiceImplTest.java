package book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import book.store.dto.category.CategoryRequestDto;
import book.store.dto.category.CategoryResponseDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.CategoryRepository;
import book.store.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository repository;
    @Mock
    private CategoryMapper mapper;
    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    @DisplayName("Checks if the category is in the database after saving")
    public void saveCategory_ReturnsCategoryDto() {
        Category model = getCategory();
        CategoryRequestDto requestDto = getCategoryRequestDto();
        CategoryResponseDto responseDto = getCategoryResponseDto();

        when(mapper.toDto(model)).thenReturn(responseDto);
        when(mapper.toModel(requestDto)).thenReturn(model);
        when(repository.save(model)).thenReturn(model.setId(1L));

        CategoryResponseDto actual = service.save(requestDto);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(actual, responseDto);
    }

    @Test
    @DisplayName("Find all categories in database")
    public void findAll_ReturnsListWithOneCategoryDto() {
        Category model = getCategory();
        CategoryResponseDto responseDto = getCategoryResponseDto();
        Pageable pageable = Pageable.unpaged();
        Page<Category> page = new PageImpl<>(List.of(model));

        when(mapper.toDto(model)).thenReturn(responseDto);
        when(repository.findAll(Pageable.unpaged())).thenReturn(page);
        List<CategoryResponseDto> actual = service.findAll(pageable);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(responseDto.getName(), actual.get(0).getName());
    }

    @Test
    @DisplayName("Get category by valid id")
    public void getCategoryById_ValidId_ReturnsCategoryDto() {
        Category model = getCategory();
        CategoryResponseDto responseDto = getCategoryResponseDto();

        when(mapper.toDto(model)).thenReturn(responseDto);
        when(repository.getReferenceById(1L)).thenReturn(model);
        CategoryResponseDto actual = service.getById(1L);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }

    @Test
    @DisplayName("Get exception after searching category by not valid id")
    public void getCategoryById_NotValidId_ReturnsException() {
        Long id = -10L;

        when(repository.getReferenceById(id)).thenThrow(new RuntimeException("error message"));

        Exception exception = assertThrows(RuntimeException.class, () -> service.getById(id));
        assertEquals("error message", exception.getMessage());
    }

    @Test
    @DisplayName("Update category with valid id and dto")
    public void updateCategory_ValidIdAndDto_ReturnsUpdatedCategoryDto() {
        Long existedId = 1L;
        Category modelFromDb = getCategory().setId(existedId);
        Category newModel = getCategory().setName("Other Category");
        CategoryRequestDto requestDto = getCategoryRequestDto();
        CategoryResponseDto responseDto = getCategoryResponseDto().setName(newModel.getName());

        when(repository.findById(existedId)).thenReturn(Optional.of(modelFromDb));
        when(mapper.toModel(requestDto)).thenReturn(newModel);
        when(mapper.toDto(newModel)).thenReturn(responseDto);
        CategoryResponseDto actual = service.update(existedId, requestDto);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }

    @Test
    @DisplayName("Update category with valid dto and not valid id")
    public void updateCategory_ValidDtoAndNotValidId_ReturnsException() {
        Long notExistedId = -1L;
        CategoryRequestDto requestDto = getCategoryRequestDto();

        when(repository.findById(notExistedId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> service.update(notExistedId, requestDto));
        assertEquals("Can't find category by id: " + notExistedId,
                exception.getMessage());
    }

    private Category getCategory() {
        return new Category()
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
