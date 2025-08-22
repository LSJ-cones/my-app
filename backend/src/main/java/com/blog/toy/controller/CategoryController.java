package com.blog.toy.controller;

import com.blog.toy.dto.ReorderRequestDto;
import com.blog.toy.dto.category.CategoryRequestDto;
import com.blog.toy.dto.category.CategoryResponseDto;
import com.blog.toy.service.CategoryService;
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
@RequestMapping("/api/categories")
@Tag(name = "카테고리 관리", description = "카테고리 CRUD API")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 중복된 카테고리명"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        System.out.println("🔍 카테고리 생성 요청: " + requestDto);
        CategoryResponseDto response = categoryService.createCategory(requestDto);
        System.out.println("🔍 카테고리 생성 응답: " + response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카테고리 목록 조회", description = "모든 활성 카테고리를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "계층 구조 카테고리 조회", description = "대분류와 소분류로 구성된 계층 구조 카테고리를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/hierarchy")
    public ResponseEntity<List<CategoryResponseDto>> getHierarchicalCategories() {
        System.out.println("🔍 계층형 카테고리 조회 요청");
        List<CategoryResponseDto> categories = categoryService.getHierarchicalCategories();
        System.out.println("🔍 계층형 카테고리 조회 응답: " + categories.size() + "개");
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "대분류 카테고리 조회", description = "모든 대분류 카테고리를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/main")
    public ResponseEntity<List<CategoryResponseDto>> getMainCategories() {
        List<CategoryResponseDto> categories = categoryService.getMainCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "소분류 카테고리 조회", description = "특정 대분류의 소분류 카테고리들을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "대분류 카테고리를 찾을 수 없음")
    })
    @GetMapping("/main/{mainCategoryId}/sub")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategories(@PathVariable Long mainCategoryId) {
        List<CategoryResponseDto> categories = categoryService.getSubCategories(mainCategoryId);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "카테고리 상세 조회", description = "특정 카테고리의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "카테고리 수정", description = "기존 카테고리를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDto requestDto) {
        System.out.println("🔍 카테고리 수정 요청 - ID: " + id + ", 데이터: " + requestDto);
        CategoryResponseDto response = categoryService.updateCategory(id, requestDto);
        System.out.println("🔍 카테고리 수정 응답: " + response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "400", description = "게시글이 있는 카테고리는 삭제할 수 없음"),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        System.out.println("🔍 카테고리 삭제 요청 - ID: " + id);
        categoryService.deleteCategory(id);
        System.out.println("🔍 카테고리 삭제 완료");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카테고리 순서 변경", description = "카테고리의 표시 순서를 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "순서 변경 성공"),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    @PutMapping("/{id}/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> reorderCategory(@PathVariable Long id, @RequestBody ReorderRequestDto requestDto) {
        CategoryResponseDto response = categoryService.reorderCategory(id, requestDto.getNewDisplayOrder());
        return ResponseEntity.ok(response);
    }
}
