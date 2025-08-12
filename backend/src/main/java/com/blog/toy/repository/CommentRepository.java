package com.blog.toy.repository;

import com.blog.toy.domain.Comment;
import com.blog.toy.domain.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 기본 댓글 조회 (대댓글 제외)
    List<Comment> findByPostIdAndParentIsNull(Long postId);
    
    // 페이징을 위한 메서드 (대댓글 제외)
    Page<Comment> findByPostIdAndParentIsNull(Long postId, Pageable pageable);
    
    // 특정 상태의 댓글 조회
    List<Comment> findByPostIdAndStatus(Long postId, CommentStatus status);
    
    // 특정 사용자의 댓글 조회
    List<Comment> findByUserId(Long userId);
    
    // 특정 사용자의 댓글 조회 (페이징)
    Page<Comment> findByUserId(Long userId, Pageable pageable);
    
    // 대댓글 조회
    List<Comment> findByParentId(Long parentId);
    
    // 대댓글 조회 (페이징)
    Page<Comment> findByParentId(Long parentId, Pageable pageable);
    
    // 활성 상태의 댓글만 조회
    List<Comment> findByPostIdAndStatusAndParentIsNull(Long postId, CommentStatus status);
    
    // 댓글 수 조회 (대댓글 제외)
    long countByPostIdAndParentIsNull(Long postId);
    
    // 특정 사용자의 댓글 수 조회
    long countByUserId(Long userId);
    
    // 대댓글 수 조회
    long countByParentId(Long parentId);
    
    // 좋아요 수로 정렬된 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL AND c.status = :status ORDER BY c.likeCount DESC")
    List<Comment> findByPostIdOrderByLikeCountDesc(@Param("postId") Long postId, @Param("status") CommentStatus status);
    
    // 최신 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL AND c.status = :status ORDER BY c.createdAt DESC")
    List<Comment> findByPostIdOrderByCreatedAtDesc(@Param("postId") Long postId, @Param("status") CommentStatus status);
    
    // 신고된 댓글 조회
    List<Comment> findByStatus(CommentStatus status);
    
    // 신고된 댓글 조회 (페이징)
    Page<Comment> findByStatus(CommentStatus status, Pageable pageable);
}
