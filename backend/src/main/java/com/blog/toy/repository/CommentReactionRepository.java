package com.blog.toy.repository;

import com.blog.toy.domain.Comment;
import com.blog.toy.domain.CommentReaction;
import com.blog.toy.domain.ReactionType;
import com.blog.toy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    
    // 특정 댓글의 특정 사용자 반응 조회
    Optional<CommentReaction> findByCommentAndUser(Comment comment, User user);
    
    // 특정 댓글의 모든 반응 조회
    List<CommentReaction> findByComment(Comment comment);
    
    // 특정 댓글의 특정 타입 반응 조회
    List<CommentReaction> findByCommentAndType(Comment comment, ReactionType type);
    
    // 특정 사용자의 모든 반응 조회
    List<CommentReaction> findByUser(User user);
    
    // 특정 댓글의 반응 수 조회
    @Query("SELECT COUNT(cr) FROM CommentReaction cr WHERE cr.comment = :comment AND cr.type = :type")
    long countByCommentAndType(@Param("comment") Comment comment, @Param("type") ReactionType type);
    
    // 특정 댓글의 반응 존재 여부 확인
    boolean existsByCommentAndUser(Comment comment, User user);
}
