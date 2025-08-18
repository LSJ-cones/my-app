package com.blog.toy.controller;

import com.blog.toy.domain.Post;
import com.blog.toy.dto.PageRequestDto;
import com.blog.toy.dto.PageResponseDto;
import com.blog.toy.dto.PostReactionDto;
import com.blog.toy.domain.ReactionType;
import com.blog.toy.dto.PostRequestDto;
import com.blog.toy.dto.PostResponseDto;
import com.blog.toy.dto.SearchRequestDto;
import com.blog.toy.service.PostService;
import com.blog.toy.service.PostReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "게시글 관리", description = "게시글 CRUD 및 페이징 API")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostReactionService postReactionService;

    @Operation(summary = "게시글 목록 조회 (페이징)", description = "페이징을 적용한 게시글 목록을 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터") })
    @GetMapping
    public PageResponseDto<PostResponseDto> getAllPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방향 (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection,
            @Parameter(description = "카테고리명 (쉼표로 구분)", example = "JAVA,Spring Boot") @RequestParam(required = false) String categories) {

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy(sortBy);
        pageRequestDto.setSortDirection(sortDirection);

        if (categories != null && !categories.trim().isEmpty()) {
            // 쉼표로 구분된 카테고리명들을 배열로 분리
            String[] categoryNames = categories.split(",");
            if (categoryNames.length == 1) {
                // 단일 카테고리인 경우 기존 메서드 사용
                return postService.findByCategoryName(categoryNames[0].trim(), pageRequestDto);
            } else {
                // 다중 카테고리인 경우 새로운 메서드 사용
                return postService.findByCategoryNames(categoryNames, pageRequestDto);
            }
        }

        return postService.findAllWithPaging(pageRequestDto);
    }

    @Operation(summary = "전체 게시글 조회 (페이징 없음)", description = "페이징 없이 모든 게시글을 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/all")
    public List<Post> getAllPostsWithoutPaging() {
        return postService.findAll();
    }

    @Operation(summary = "개별 게시글 조회", description = "ID로 특정 게시글을 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음") })
    @GetMapping("/{id}")
    public PostResponseDto getPostById(@Parameter(description = "게시글 ID", example = "1") @PathVariable Long id) {
        // 조회수 증가
        postService.incrementViewCount(id);
        return postService.getPostWithComments(id);
    }

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터") })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        PostResponseDto response = postService.createPost(postRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터") })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponseDto> updatePost(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody PostRequestDto postRequestDto) {
        PostResponseDto response = postService.updatePost(id, postRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음") })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@Parameter(description = "게시글 ID", example = "1") @PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 검색 (페이징)", description = "키워드로 게시글을 검색하고 페이징을 적용합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터") })
    @GetMapping("/search")
    public PageResponseDto<PostResponseDto> searchPosts(
            @Parameter(description = "검색 키워드", example = "스프링", required = true) @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방향 (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection) {

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy(sortBy);
        pageRequestDto.setSortDirection(sortDirection);

        return postService.searchWithPaging(keyword, pageRequestDto);
    }

    @Operation(summary = "게시글 검색 (페이징 없음)", description = "키워드로 게시글을 검색합니다 (페이징 없음).")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "검색 성공") })
    @GetMapping("/search/all")
    public List<Post> searchPostsWithoutPaging(
            @Parameter(description = "검색 키워드", example = "스프링", required = true) @RequestParam String keyword) {
        return postService.search(keyword);
    }

    @Operation(summary = "카테고리별 게시글 조회", description = "특정 카테고리의 게시글을 페이징하여 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음") })
    @GetMapping("/category/{categoryId}")
    public PageResponseDto<PostResponseDto> getPostsByCategory(
            @Parameter(description = "카테고리 ID", example = "1") @PathVariable Long categoryId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy("createdAt");
        pageRequestDto.setSortDirection("desc");

        return postService.findByCategory(categoryId, pageRequestDto);
    }

    @Operation(summary = "태그별 게시글 조회", description = "특정 태그들의 게시글을 페이징하여 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/tags")
    public PageResponseDto<PostResponseDto> getPostsByTags(
            @Parameter(description = "태그명 목록", example = "spring,java") @RequestParam List<String> tagNames,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy("createdAt");
        pageRequestDto.setSortDirection("desc");

        return postService.findByTags(tagNames, pageRequestDto);
    }

    @Operation(summary = "상태별 게시글 조회", description = "특정 상태의 게시글을 페이징하여 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/status/{status}")
    public PageResponseDto<PostResponseDto> getPostsByStatus(
            @Parameter(description = "게시글 상태", example = "PUBLISHED") @PathVariable Post.PostStatus status,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy("createdAt");
        pageRequestDto.setSortDirection("desc");

        return postService.findByStatus(status, pageRequestDto);
    }

    // === 고급 검색 기능 ===

    @Operation(summary = "고급 검색", description = "키워드, 카테고리, 태그, 상태를 조합한 고급 검색을 수행합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터") })
    @PostMapping("/advanced-search")
    public PageResponseDto<PostResponseDto> advancedSearch(@Valid @RequestBody SearchRequestDto searchRequestDto) {
        return postService.advancedSearch(searchRequestDto);
    }

    @Operation(summary = "인기 게시글 조회", description = "조회수가 높은 게시글을 조회합니다 (최근 30일).")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/popular")
    public PageResponseDto<PostResponseDto> getPopularPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy("viewCount");
        pageRequestDto.setSortDirection("desc");

        return postService.findPopularPosts(pageRequestDto);
    }

    @Operation(summary = "최근 게시글 조회", description = "최근 7일 내에 작성된 게시글을 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/recent")
    public PageResponseDto<PostResponseDto> getRecentPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy("createdAt");
        pageRequestDto.setSortDirection("desc");

        return postService.findRecentPosts(pageRequestDto);
    }

    @Operation(summary = "댓글 많은 순 게시글 조회", description = "댓글 수가 많은 순으로 게시글을 조회합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/most-commented")
    public PageResponseDto<PostResponseDto> getMostCommentedPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);
        pageRequestDto.setSortBy("commentCount");
        pageRequestDto.setSortDirection("desc");

        return postService.findPostsByCommentCount(pageRequestDto);
    }

    @Operation(summary = "게시글 반응 (좋아요/싫어요)", description = "게시글에 좋아요 또는 싫어요를 누릅니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "반응 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터") })
    @PostMapping("/{postId}/reaction")
    public ResponseEntity<PostResponseDto> reactToPost(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @Valid @RequestBody PostReactionDto reactionDto) {

        reactionDto.setPostId(postId);
        PostResponseDto response = postService.reactToPost(reactionDto);
        return ResponseEntity.ok(response);
    }
}
