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
    Page<Post> findByCategoryAndStatus(Category category, Post.PostStatus status, Pageable pageable);
    
    // 태그 관련 메소드들
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t IN :tags")
    Page<Post> findByTags(@Param("tags") List<Tag> tags, Pageable pageable);
    
    // 상태 관련 메소드들
    Page<Post> findByStatus(Post.PostStatus status, Pageable pageable);
    
    // 조회수 정렬
    Page<Post> findAllByOrderByViewCountDesc(Pageable pageable);
}
