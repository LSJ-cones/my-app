package com.blog.toy.repository;

import com.blog.toy.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// DB와 상호작용하는 Repository 인터페이스(DB와 직접 통신하는 역할만함)
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleContainingOrContentContaining(String title, String content);
}
