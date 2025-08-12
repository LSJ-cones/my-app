package com.blog.toy.repository;

import com.blog.toy.domain.Post;
import com.blog.toy.domain.PostReaction;
import com.blog.toy.domain.ReactionType;
import com.blog.toy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    
    // 특정 게시글과 사용자의 반응 조회
    Optional<PostReaction> findByPostAndUser(Post post, User user);
    
    // 특정 게시글의 특정 타입 반응 개수
    long countByPostAndType(Post post, ReactionType type);
    
    // 특정 게시글의 모든 반응 삭제
    void deleteByPost(Post post);
    
    // 특정 사용자의 모든 반응 삭제
    void deleteByUser(User user);
}
