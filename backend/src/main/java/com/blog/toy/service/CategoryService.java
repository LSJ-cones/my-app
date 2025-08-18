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

        // displayOrder 자동 설정
        Integer displayOrder = requestDto.getDisplayOrder();
        if (displayOrder == null) {
            // 같은 타입의 카테고리 중 가장 큰 displayOrder + 1
            Integer maxOrder = categoryRepository.findMaxDisplayOrderByCategoryType(requestDto.getCategoryType());
            displayOrder = (maxOrder != null) ? maxOrder + 1 : 1;
        }

        Category category = Category.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .displayOrder(displayOrder)
                .active(requestDto.isActive())
                .categoryType(requestDto.getCategoryType())
                .build();

        // 부모 카테고리가 지정된 경우
        if (requestDto.getParentId() != null) {
            Category parent = categoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 카테고리를 찾을 수 없습니다: " + requestDto.getParentId()));
            category.setParent(parent);
        }

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
    public List<CategoryResponseDto> getHierarchicalCategories() {
        return categoryRepository.findAllActiveOrdered()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getMainCategories() {
        return categoryRepository.findByCategoryTypeAndActiveTrueOrderByDisplayOrder(Category.CategoryType.MAIN)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getSubCategories(Long mainCategoryId) {
        Category mainCategory = categoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new RuntimeException("대분류 카테고리를 찾을 수 없습니다: " + mainCategoryId));
        
        return categoryRepository.findByParentAndActiveTrueOrderByDisplayOrder(mainCategory)
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
        category.setCategoryType(requestDto.getCategoryType());

        // 부모 카테고리 업데이트
        if (requestDto.getParentId() != null) {
            Category parent = categoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 카테고리를 찾을 수 없습니다: " + requestDto.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

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

        // 하위 카테고리가 있는지 확인 (대분류인 경우)
        if (category.isMainCategory() && !category.getChildren().isEmpty()) {
            throw new RuntimeException("하위 카테고리가 있는 대분류는 삭제할 수 없습니다.");
        }

        categoryRepository.delete(category);
    }

    public CategoryResponseDto reorderCategory(Long id, Integer newDisplayOrder) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + id));

        Integer oldDisplayOrder = category.getDisplayOrder();
        
        // 같은 타입의 카테고리들을 가져와서 순서 조정
        List<Category> sameTypeCategories = categoryRepository.findByCategoryTypeAndActiveTrueOrderByDisplayOrder(category.getCategoryType());
        
        if (oldDisplayOrder < newDisplayOrder) {
            // 뒤로 이동하는 경우: 기존 순서와 새 순서 사이의 카테고리들을 앞으로 한 칸씩 이동
            for (Category cat : sameTypeCategories) {
                if (cat.getId().equals(id)) continue; // 현재 카테고리는 건너뛰기
                if (cat.getDisplayOrder() > oldDisplayOrder && cat.getDisplayOrder() <= newDisplayOrder) {
                    cat.setDisplayOrder(cat.getDisplayOrder() - 1);
                    categoryRepository.save(cat);
                }
            }
        } else if (oldDisplayOrder > newDisplayOrder) {
            // 앞으로 이동하는 경우: 새 순서와 기존 순서 사이의 카테고리들을 뒤로 한 칸씩 이동
            for (Category cat : sameTypeCategories) {
                if (cat.getId().equals(id)) continue; // 현재 카테고리는 건너뛰기
                if (cat.getDisplayOrder() >= newDisplayOrder && cat.getDisplayOrder() < oldDisplayOrder) {
                    cat.setDisplayOrder(cat.getDisplayOrder() + 1);
                    categoryRepository.save(cat);
                }
            }
        }
        
        // 현재 카테고리의 순서 설정
        category.setDisplayOrder(newDisplayOrder);
        Category updatedCategory = categoryRepository.save(category);
        
        return convertToResponseDto(updatedCategory);
    }

    private CategoryResponseDto convertToResponseDto(Category category) {
        long postCount = postRepository.countByCategory(category);
        
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .active(category.isActive())
                .categoryType(category.getCategoryType())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .fullPath(category.getFullPath())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .postCount(postCount)
                .build();
    }
}
