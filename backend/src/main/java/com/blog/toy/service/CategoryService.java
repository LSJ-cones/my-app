package com.blog.toy.service;

import com.blog.toy.domain.Category;
import com.blog.toy.dto.category.CategoryRequestDto;
import com.blog.toy.dto.category.CategoryResponseDto;
import com.blog.toy.repository.CategoryRepository;
import com.blog.toy.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new RuntimeException("이미 존재하는 카테고리명입니다: " + requestDto.getName());
        }

        Category category = Category.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .displayOrder(requestDto.getDisplayOrder())
                .active(requestDto.isActive())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return convertToResponseDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAllActiveOrdered()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + id));
        return convertToResponseDto(category);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + id));

        // 이름이 변경된 경우 중복 체크
        if (!category.getName().equals(requestDto.getName()) && 
            categoryRepository.existsByName(requestDto.getName())) {
            throw new RuntimeException("이미 존재하는 카테고리명입니다: " + requestDto.getName());
        }

        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        category.setDisplayOrder(requestDto.getDisplayOrder());
        category.setActive(requestDto.isActive());

        Category updatedCategory = categoryRepository.save(category);
        return convertToResponseDto(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + id));

        // 해당 카테고리의 게시글 수 확인
        long postCount = postRepository.countByCategory(category);
        if (postCount > 0) {
            throw new RuntimeException("게시글이 있는 카테고리는 삭제할 수 없습니다. 게시글 수: " + postCount);
        }

        categoryRepository.delete(category);
    }

    private CategoryResponseDto convertToResponseDto(Category category) {
        long postCount = postRepository.countByCategory(category);
        
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .postCount(postCount)
                .build();
    }
}
