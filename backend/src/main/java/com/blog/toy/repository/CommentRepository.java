package com.blog.toy.repository;

import com.blog.toy.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    
    // 페이징을 위한 메서드
    Page<Comment> findByPostId(Long postId, Pageable pageable);
}
