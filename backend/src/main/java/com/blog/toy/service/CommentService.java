package com.blog.toy.service;

import java.util.stream.Collectors;
import com.blog.toy.domain.Comment;
import com.blog.toy.dto.CommentResponseDto;
import com.blog.toy.dto.PageRequestDto;
import com.blog.toy.dto.PageResponseDto;
import com.blog.toy.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    // CommentService는 댓글 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.

    // 이 클래스는 댓글 조회, 추가, 삭제 등의 기능을 제공합니다.
    private final CommentRepository commentRepository;

    // 생성자 주입을 통해 CommentRepository를 주입받습니다.
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // 댓글 조회: 특정 게시글에 대한 댓글을 조회합니다.
    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(comment -> CommentResponseDto.builder().id(comment.getId()).author(comment.getAuthor())
                        .content(comment.getContent()).createdAt(comment.getCreatedAt()).build())
                .collect(Collectors.toList());
    }
    
    // 댓글 조회 (페이징 적용): 특정 게시글에 대한 댓글을 페이징하여 조회합니다.
    public PageResponseDto<CommentResponseDto> getCommentsByPostIdWithPaging(Long postId, PageRequestDto pageRequestDto) {
        Page<Comment> commentPage = commentRepository.findByPostId(postId, pageRequestDto.toPageable());
        
        Page<CommentResponseDto> commentResponseDtoPage = commentPage.map(comment -> 
            CommentResponseDto.builder()
                .id(comment.getId())
                .author(comment.getAuthor())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build()
        );
        
        return new PageResponseDto<>(commentResponseDtoPage);
    }

    // 댓글 추가: 댓글을 추가하고, 생성일시를 현재 시간으로 설정합니다.
    public Comment addComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    // 댓글 삭제: 댓글 ID를 통해 댓글을 삭제합니다.
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
