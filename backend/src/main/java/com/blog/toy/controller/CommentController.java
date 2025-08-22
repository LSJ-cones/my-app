package com.blog.toy.controller;

import com.blog.toy.domain.ReportStatus;
import com.blog.toy.dto.*;
import com.blog.toy.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "댓글 관리", description = "댓글 CRUD, 대댓글, 좋아요/싫어요, 신고 기능 API")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "게시글 댓글 목록 조회", description = "특정 게시글의 댓글을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<PageResponseDto<CommentResponseDto>> getCommentsByPost(
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
        
        PageResponseDto<CommentResponseDto> response = commentService.getCommentsByPostIdWithPaging(postId, pageRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 댓글 목록 조회 (페이징 없음)", description = "특정 게시글의 모든 댓글을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/post/{postId}/all")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPostWithoutPaging(
            @Parameter(description = "게시글 ID", example = "1") 
            @PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 생성", description = "새로운 댓글을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "댓글 생성 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @Parameter(description = "댓글 생성 요청") 
            @Valid @RequestBody CommentRequestDto requestDto) {
        CommentResponseDto createdComment = commentService.createComment(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @Parameter(description = "댓글 ID", example = "1") 
            @PathVariable Long commentId,
            @Parameter(description = "댓글 수정 요청") 
            @Valid @RequestBody CommentRequestDto requestDto) {
        CommentResponseDto updatedComment = commentService.updateComment(commentId, requestDto);
        return ResponseEntity.ok(updatedComment);
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", example = "1") 
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 좋아요/싫어요", description = "댓글에 좋아요 또는 싫어요를 표시합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "반응 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
        @PostMapping("/{commentId}/reaction")
    public ResponseEntity<CommentResponseDto> reactToComment(
            @Parameter(description = "댓글 ID", example = "1")
            @PathVariable Long commentId,
            @Parameter(description = "반응 요청")
            @RequestBody Map<String, Object> requestBody) {
        
        System.out.println("🔍 댓글 반응 요청 - commentId: " + commentId);
        System.out.println("🔍 댓글 반응 요청 - requestBody: " + requestBody);
        
        // JSON에서 type 값을 직접 추출
        // requestBody에서 type 추출 (type과 reactionType 둘 다 지원)
        Object typeObj = requestBody.get("type");
        if (typeObj == null) {
            typeObj = requestBody.get("reactionType"); // fallback
        }
        System.out.println("🔍 댓글 반응 요청 - type 객체: " + typeObj);
        System.out.println("🔍 댓글 반응 요청 - type 클래스: " + (typeObj != null ? typeObj.getClass() : "null"));
        
        String typeString = typeObj != null ? typeObj.toString() : null;
        System.out.println("🔍 댓글 반응 요청 - type 문자열: " + typeString);
        
        // CommentReactionDto 수동 생성
        CommentReactionDto reactionDto = new CommentReactionDto();
        reactionDto.setCommentId(commentId);
        reactionDto.setType(typeString);
        
        System.out.println("🔍 댓글 반응 요청 - 생성된 reactionDto: " + reactionDto);
        System.out.println("🔍 댓글 반응 요청 - reactionDto.getType(): " + reactionDto.getType());

        CommentResponseDto response = commentService.reactToComment(reactionDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "댓글 신고", description = "부적절한 댓글을 신고합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "신고 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/{commentId}/report")
    public ResponseEntity<Void> reportComment(
            @Parameter(description = "댓글 ID", example = "1") 
            @PathVariable Long commentId,
            @Parameter(description = "신고 요청") 
            @Valid @RequestBody CommentReportDto reportDto) {
        reportDto.setCommentId(commentId);
        commentService.reportComment(reportDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "대댓글 조회", description = "특정 댓글의 대댓글을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponseDto>> getReplies(
            @Parameter(description = "댓글 ID", example = "1") 
            @PathVariable Long commentId) {
        List<CommentResponseDto> replies = commentService.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(replies);
    }

    @Operation(summary = "사용자 댓글 조회", description = "특정 사용자의 댓글을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponseDto<CommentResponseDto>> getUserComments(
            @Parameter(description = "사용자 ID", example = "1") 
            @PathVariable Long userId,
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
        
        PageResponseDto<CommentResponseDto> response = commentService.getUserComments(userId, pageRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "신고된 댓글 조회 (관리자용)", description = "신고된 댓글 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/reported")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponseDto<CommentResponseDto>> getReportedComments(
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
        
        PageResponseDto<CommentResponseDto> response = commentService.getReportedComments(pageRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "신고 처리 (관리자용)", description = "신고를 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "처리 성공"),
        @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping("/reports/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> handleReport(
            @Parameter(description = "신고 ID", example = "1") 
            @PathVariable Long reportId,
            @Parameter(description = "처리 상태", example = "RESOLVED") 
            @RequestParam ReportStatus status) {
        commentService.handleReport(reportId, status);
        return ResponseEntity.ok().build();
    }
}
