package com.blog.toy.controller;

import com.blog.toy.dto.tag.TagRequestDto;
import com.blog.toy.dto.tag.TagResponseDto;
import com.blog.toy.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/tags")
@Tag(name = "태그 관리", description = "태그 CRUD API")
public class TagController {

    @Autowired
    private TagService tagService;

    @Operation(summary = "태그 생성", description = "새로운 태그를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 중복된 태그명")
    })
    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") // 임시로 주석 처리
    public ResponseEntity<TagResponseDto> createTag(@Valid @RequestBody TagRequestDto requestDto) {
        TagResponseDto response = tagService.createTag(requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "태그 목록 조회", description = "모든 활성 태그를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        List<TagResponseDto> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "태그 상세 조회", description = "특정 태그의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable Long id) {
        TagResponseDto tag = tagService.getTagById(id);
        return ResponseEntity.ok(tag);
    }

    @Operation(summary = "태그 수정", description = "기존 태그를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // 임시로 주석 처리
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequestDto requestDto) {
        TagResponseDto response = tagService.updateTag(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "태그 삭제", description = "태그를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "400", description = "게시글이 있는 태그는 삭제할 수 없음"),
        @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // 임시로 주석 처리
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok().build();
    }
}
