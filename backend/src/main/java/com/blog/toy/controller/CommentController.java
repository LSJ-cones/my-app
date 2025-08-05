package com.blog.toy.controller;

import com.blog.toy.domain.Comment;
import com.blog.toy.domain.Post;
import com.blog.toy.dto.CommentResponseDto;
import com.blog.toy.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 생성자 주입을 통해 CommentService를 주입받음
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 조회, 추가, 삭제 기능을 제공하는 엔드포인트
    @GetMapping
    public List<CommentResponseDto> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    // 댓글 추가
    @PostMapping
    public Comment addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        // Post ID만 설정하면 DB에 연관관계 설정 가능
        comment.setPost(Post.builder().id(postId).build());
        return commentService.addComment(comment);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
