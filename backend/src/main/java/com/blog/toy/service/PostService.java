package com.blog.toy.service;

import com.blog.toy.domain.Post;
import com.blog.toy.dto.CommentResponseDto;
import com.blog.toy.dto.PostResponseDto;
import com.blog.toy.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    // 생성자 주입을 통해 PostRepository를 주입받음
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 전체 게시글 조회
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    // 개별 게시글 조회
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    // 게시글 생성 및 수정
    public Post save(Post post) {
        return postRepository.save(post);
    }

    // 게시글 삭제
    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    // 게시글 검색
    public List<Post> search(String keyword) {
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }
   
    // 게시글과 해당 게시글의 댓글을 포함한 DTO 반환
    // 이 메서드는 게시글 ID를 통해 게시글과 댓글 정보를 포함한 PostResponseDto를 반환합니다.
       public PostResponseDto getPostWithComments(Long id) {
        Post post = postRepository.findById(id).orElseThrow();

        List<CommentResponseDto> commentDtos = post.getComments().stream()
            .map(comment -> CommentResponseDto.builder()
                .id(comment.getId())
                .author(comment.getAuthor())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        return PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .author(post.getAuthor())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .comments(commentDtos)
            .build();
    }

}
