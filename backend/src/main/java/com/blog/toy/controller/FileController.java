package com.blog.toy.controller;

import com.blog.toy.dto.file.FileResponseDto;
import com.blog.toy.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@Tag(name = "파일 관리", description = "파일 업로드, 다운로드, 관리 API")
public class FileController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "단일 파일 업로드", description = "게시글에 단일 파일을 업로드합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PostMapping("/upload")
    public ResponseEntity<FileResponseDto> uploadFile(
            @Parameter(description = "업로드할 파일", required = true, content = @Content(mediaType = "multipart/form-data"))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "게시글 ID (선택사항)")
            @RequestParam(value = "postId", required = false) Long postId) throws IOException {
        
        String username = getCurrentUsername();
        FileResponseDto response = fileService.uploadFile(file, postId, username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "다중 파일 업로드", description = "게시글에 여러 파일을 업로드합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PostMapping("/upload/multiple")
    public ResponseEntity<List<FileResponseDto>> uploadMultipleFiles(
            @Parameter(description = "업로드할 파일들", required = true, content = @Content(mediaType = "multipart/form-data"))
            @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "게시글 ID (선택사항)")
            @RequestParam(value = "postId", required = false) Long postId) throws IOException {
        
        String username = getCurrentUsername();
        List<FileResponseDto> response = fileService.uploadMultipleFiles(files, postId, username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "파일 다운로드", description = "파일을 다운로드합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "다운로드 성공"),
        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
    })
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws IOException {
        FileResponseDto fileInfo = fileService.getFileInfo(fileId);
        Resource resource = fileService.downloadFile(fileId);
        
        // 원본 파일명 인코딩
        String filename = URLEncoder.encode(fileInfo.getOriginalFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .body(resource);
    }

    @Operation(summary = "게시글별 파일 목록 조회", description = "특정 게시글의 파일 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<FileResponseDto>> getFilesByPost(@PathVariable Long postId) {
        List<FileResponseDto> files = fileService.getFilesByPost(postId);
        return ResponseEntity.ok(files);
    }

    @Operation(summary = "사용자별 파일 목록 조회", description = "현재 로그인한 사용자의 파일 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/my-files")
    public ResponseEntity<List<FileResponseDto>> getMyFiles() {
        String username = getCurrentUsername();
        List<FileResponseDto> files = fileService.getFilesByUser(username);
        return ResponseEntity.ok(files);
    }

    @Operation(summary = "파일 삭제", description = "파일을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) throws IOException {
        String username = getCurrentUsername();
        fileService.deleteFile(fileId, username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 파일 전체 삭제", description = "게시글의 모든 파일을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deleteFilesByPost(@PathVariable Long postId) throws IOException {
        fileService.deleteFilesByPost(postId);
        return ResponseEntity.ok().build();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            // 임시로 admin 사용자 반환 (테스트용)
            return "admin";
        }
        return authentication.getName();
    }
}
