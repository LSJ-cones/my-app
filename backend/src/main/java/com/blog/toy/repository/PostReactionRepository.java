package com.blog.toy.repository;

import com.blog.toy.domain.Post;
import com.blog.toy.domain.PostReaction;
import com.blog.toy.domain.ReactionType;
import com.blog.toy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    // 특정 게시글의 모든 반응 조회
    List<PostReaction> findByPost(Post post);

    // 특정 사용자의 모든 반응 조회
    List<PostReaction> findByUser(User user);

    // 특정 게시글의 특정 타입 반응 조회
    List<PostReaction> findByPostAndType(Post post, ReactionType type);

    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    boolean existsByPostAndUserAndType(Post post, User user, ReactionType type);

    // 특정 게시글의 반응 통계 조회
    @Query("SELECT " + "COUNT(CASE WHEN pr.type = 'LIKE' THEN 1 END) as likeCount, "
            + "COUNT(CASE WHEN pr.type = 'DISLIKE' THEN 1 END) as dislikeCount "
            + "FROM PostReaction pr WHERE pr.post = :post")
    Object[] getReactionStats(@Param("post") Post post);

    // 사용자의 특정 게시글 반응 타입 조회
    @Query("SELECT pr.type FROM PostReaction pr WHERE pr.post = :post AND pr.user = :user")
    Optional<ReactionType> findUserReactionType(@Param("post") Post post, @Param("user") User user);
}
