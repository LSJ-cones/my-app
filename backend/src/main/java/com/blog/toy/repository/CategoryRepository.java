package com.blog.toy.repository;

import com.blog.toy.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    List<Category> findByActiveOrderByDisplayOrderAscNameAsc(boolean active);
    
    boolean existsByName(String name);
    
    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findAllActiveOrdered();
    
    // 계층 구조 지원 메서드들
    List<Category> findByCategoryTypeAndActiveTrueOrderByDisplayOrder(Category.CategoryType categoryType);
    
    List<Category> findByParentAndActiveTrueOrderByDisplayOrder(Category parent);
    
    List<Category> findByParentIsNullAndActiveTrueOrderByDisplayOrder();
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findAllMainCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent = :parent AND c.active = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findSubCategoriesByParent(Category parent);
    
    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.categoryType = :categoryType")
    Integer findMaxDisplayOrderByCategoryType(Category.CategoryType categoryType);
}
