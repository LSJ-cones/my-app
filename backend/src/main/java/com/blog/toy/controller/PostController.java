package com.blog.toy.controller;

import com.blog.toy.domain.Post;
import com.blog.toy.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 전체 게시글 조회
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.findAll();
    }

    // 개별 게시글 조회, 생성, 수정, 삭제
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.findById(id).orElseThrow();
    }

    // 게시글 생성
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.save(post);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post post) {
        Post existing = postService.findById(id).orElseThrow();
        existing.setTitle(post.getTitle());
        existing.setContent(post.getContent());
        existing.setAuthor(post.getAuthor());
        return postService.save(existing);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.delete(id);
    }

    // 게시글 검색
    @GetMapping("/search")
    public List<Post> searchPosts(@RequestParam String keyword) {
        return postService.search(keyword);
    }
}
