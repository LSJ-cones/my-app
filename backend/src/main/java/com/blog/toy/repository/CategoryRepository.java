package com.blog.toy.repository;

import com.blog.toy.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Category> findByParentIsNullAndActiveTrueOrderByDisplayOrder();
    
    List<Category> findByParentAndActiveTrueOrderByDisplayOrder(Category parent);
    
    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.parent.id = :parentId")
    Integer findMaxDisplayOrderByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.parent IS NULL")
    Integer findMaxDisplayOrderForMainCategories();
    
    // 카테고리 타입별 조회 (하위 호환성을 위해 유지)
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true ORDER BY c.displayOrder ASC")
    List<Category> findMainCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NOT NULL AND c.active = true ORDER BY c.displayOrder ASC")
    List<Category> findSubCategories();
}
