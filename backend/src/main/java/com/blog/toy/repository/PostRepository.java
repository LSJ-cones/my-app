package com.blog.toy.repository;

import com.blog.toy.domain.Category;
import com.blog.toy.domain.Post;
import com.blog.toy.domain.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// DB와 상호작용하는 Repository 인터페이스(DB와 직접 통신하는 역할만함)
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleContainingOrContentContaining(String title, String content);
    
    // 페이징을 위한 메서드들
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    
    // 카테고리 관련 메소드들
    long countByCategory(Category category);
    Page<Post> findByCategory(Category category, Pageable pageable);
    Page<Post> findByCategoryIn(List<Category> categories, Pageable pageable);
    Page<Post> findByCategoryAndStatus(Category category, Post.PostStatus status, Pageable pageable);
    
    // 태그 관련 메소드들
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t IN :tags")
    Page<Post> findByTags(@Param("tags") List<Tag> tags, Pageable pageable);
    
    // 상태 관련 메소드들
    Page<Post> findByStatus(Post.PostStatus status, Pageable pageable);
    
    // 조회수 정렬
    Page<Post> findAllByOrderByViewCountDesc(Pageable pageable);
    
    // === 고급 검색 기능 ===
    
    // 키워드 검색 (제목 또는 내용)
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 키워드 + 카테고리 검색
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> findByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
    
    // 키워드 + 태그 검색
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.name IN :tagNames AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> findByKeywordAndTags(@Param("keyword") String keyword, @Param("tagNames") List<String> tagNames, Pageable pageable);
    
    // 키워드 + 상태 검색
    @Query("SELECT p FROM Post p WHERE p.status = :status AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") Post.PostStatus status, Pageable pageable);
    
    // 복합 검색 (키워드 + 카테고리 + 태그 + 상태)
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
           "(:keyword IS NULL OR (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:tagNames IS NULL OR t.name IN :tagNames) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Post> findByAdvancedSearch(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("tagNames") List<String> tagNames,
            @Param("status") Post.PostStatus status,
            Pageable pageable);
    
    // 태그 이름으로 검색
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.name IN :tagNames")
    Page<Post> findByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);
    
    // 카테고리 ID로 검색
    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);
    
    // 댓글 수로 정렬 (댓글 수가 많은 순)
    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p ORDER BY COUNT(c) DESC")
    Page<Post> findAllOrderByCommentCountDesc(Pageable pageable);
    
    // 최근 게시글 (최근 7일)
    @Query("SELECT p FROM Post p WHERE p.createdAt >= :sevenDaysAgo")
    Page<Post> findRecentPosts(@Param("sevenDaysAgo") java.time.LocalDateTime sevenDaysAgo, Pageable pageable);
    
    // 인기 게시글 (조회수 높은 순, 최근 30일)
    @Query("SELECT p FROM Post p WHERE p.createdAt >= :thirtyDaysAgo ORDER BY p.viewCount DESC")
    Page<Post> findPopularPosts(@Param("thirtyDaysAgo") java.time.LocalDateTime thirtyDaysAgo, Pageable pageable);
}
