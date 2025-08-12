package com.blog.toy.controller;

import com.blog.toy.domain.ReactionType;
import com.blog.toy.dto.PostReactionDto;
import com.blog.toy.repository.PostReactionRepository;
import com.blog.toy.service.PostReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/reactions")
@RequiredArgsConstructor
@Tag(name = "게시글 반응", description = "게시글 좋아요/싫어요 관련 API")
public class PostReactionController {

    private final PostReactionService postReactionService;
    private final PostReactionRepository postReactionRepository;

    @PostMapping
    @Operation(summary = "게시글 반응 추가/수정", description = "게시글에 좋아요 또는 싫어요를 추가하거나 수정합니다.")
    public ResponseEntity<PostReactionDto> addReaction(@Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "반응 타입 (LIKE, DISLIKE)") @RequestBody ReactionRequest request) {
        PostReactionDto reaction = postReactionService.addReaction(postId, request.getType());
        return ResponseEntity.ok(reaction);
    }

    @DeleteMapping
    @Operation(summary = "게시글 반응 취소", description = "게시글에 추가한 반응을 취소합니다.")
    public ResponseEntity<PostReactionDto> removeReaction(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        PostReactionDto reaction = postReactionService.removeReaction(postId);
        return ResponseEntity.ok(reaction);
    }

    @GetMapping
    @Operation(summary = "게시글 반응 조회", description = "현재 사용자의 게시글 반응 상태와 통계를 조회합니다.")
    public ResponseEntity<PostReactionDto> getReaction(@Parameter(description = "게시글 ID") @PathVariable Long postId) {
        try {
            PostReactionDto reaction = postReactionService.getReaction(postId);
            return ResponseEntity.ok(reaction);
        } catch (Exception e) {
            // 에러 로깅
            System.err.println("게시글 반응 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            // 더 자세한 에러 정보 반환
            return ResponseEntity.status(500).body(PostReactionDto.builder().postId(postId).postTitle("에러 발생")
                    .username("에러").likeCount(0L).dislikeCount(0L).userLiked(false).userDisliked(false).build());
        }
    }

    @GetMapping("/test")
    @Operation(summary = "게시글 반응 테스트", description = "PostReaction 테이블 접근 테스트")
    public ResponseEntity<String> testReaction(@Parameter(description = "게시글 ID") @PathVariable Long postId) {
        try {
            // 간단한 테스트
            long count = postReactionRepository.count();
            return ResponseEntity.ok("PostReaction 테이블 접근 성공. 총 반응 수: " + count);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("PostReaction 테이블 접근 실패: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "게시글의 모든 반응 조회", description = "특정 게시글의 모든 반응 목록을 조회합니다.")
    public ResponseEntity<List<PostReactionDto>> getPostReactions(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        List<PostReactionDto> reactions = postReactionService.getPostReactions(postId);
        return ResponseEntity.ok(reactions);
    }

    @GetMapping("/stats")
    @Operation(summary = "게시글 반응 통계", description = "게시글의 좋아요/싫어요 통계를 조회합니다.")
    public ResponseEntity<PostReactionDto> getReactionStats(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        PostReactionDto stats = postReactionService.getReactionStats(postId);
        return ResponseEntity.ok(stats);
    }

    // 반응 요청을 위한 DTO 클래스
    public static class ReactionRequest {
        private ReactionType type;

        public ReactionType getType() {
            return type;
        }

        public void setType(ReactionType type) {
            this.type = type;
        }
    }
}

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
@Tag(name = "사용자 반응", description = "사용자별 반응 관리 API")
class UserReactionController {

    private final PostReactionService postReactionService;

    @GetMapping("/my")
    @Operation(summary = "내 반응 목록", description = "현재 사용자가 추가한 모든 게시글 반응을 조회합니다.")
    public ResponseEntity<List<PostReactionDto>> getMyReactions() {
        List<PostReactionDto> reactions = postReactionService.getUserReactions();
        return ResponseEntity.ok(reactions);
    }
}
