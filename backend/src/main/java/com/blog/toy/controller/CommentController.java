package com.blog.toy.controller;

import com.blog.toy.domain.Comment;
import com.blog.toy.domain.Post;
import com.blog.toy.dto.CommentResponseDto;
import com.blog.toy.dto.PageRequestDto;
import com.blog.toy.dto.PageResponseDto;
import com.blog.toy.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@Tag(name = "댓글 관리", description = "댓글 CRUD 및 페이징 API")
public class CommentController {

    private final CommentService commentService;

    // 생성자 주입을 통해 CommentService를 주입받음
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "댓글 목록 조회 (페이징)", description = "특정 게시글의 댓글을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping
    public PageResponseDto<CommentResponseDto> getComments(
            @Parameter(description = "게시글 ID", example = "1") 
            @PathVariable Long postId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드", example = "createdAt") 
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방향 (asc/desc)", example = "desc") 
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy(sortBy);
        pageRequestDto.setSortDirection(sortDirection);
        
        return commentService.getCommentsByPostIdWithPaging(postId, pageRequestDto);
    }
    
    @Operation(summary = "댓글 목록 조회 (페이징 없음)", description = "특정 게시글의 모든 댓글을 조회합니다 (페이징 없음).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/all")
    public List<CommentResponseDto> getCommentsWithoutPaging(
            @Parameter(description = "게시글 ID", example = "1") 
            @PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @Operation(summary = "댓글 추가", description = "특정 게시글에 새로운 댓글을 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "댓글 추가 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping
    public Comment addComment(
            @Parameter(description = "게시글 ID", example = "1") 
            @PathVariable Long postId, 
            @RequestBody Comment comment) {
        // Post ID만 설정하면 DB에 연관관계 설정 가능
        comment.setPost(Post.builder().id(postId).build());
        return commentService.addComment(comment);
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @Parameter(description = "댓글 ID", example = "1") 
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
