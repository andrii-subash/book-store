package book.store.service.impl;

import book.store.dto.category.CategoryRequestDto;
import book.store.dto.category.CategoryResponseDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.CategoryRepository;
import book.store.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository.getReferenceById(id));
    }

    @Override
    public CategoryResponseDto save(CategoryRequestDto categoryDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toModel(categoryDto)));
    }

    @Override
    public CategoryResponseDto update(Long id, CategoryRequestDto categoryDto) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Can't find category by id: " + id);
        }
        Category category = categoryMapper.toModel(categoryDto);
        category.setId(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
