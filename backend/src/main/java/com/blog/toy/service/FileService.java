package com.blog.toy.service;

import com.blog.toy.domain.File;
import com.blog.toy.domain.Post;
import com.blog.toy.domain.User;
import com.blog.toy.dto.file.FileResponseDto;
import com.blog.toy.repository.FileRepository;
import com.blog.toy.repository.PostRepository;
import com.blog.toy.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Value("${app.file.allowed-extensions}")
    private String allowedExtensions;

    public FileResponseDto uploadFile(MultipartFile multipartFile, Long postId, String username) throws IOException {
        // 파일 유효성 검사
        validateFile(multipartFile);

        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일명 생성 (중복 방지)
        String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        String fileExtension = FilenameUtils.getExtension(originalFileName);
        String storedFileName = generateStoredFileName(fileExtension);

        // 파일 저장
        Path targetLocation = uploadPath.resolve(storedFileName);
        Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // DB에 파일 정보 저장
        Post post = null;
        if (postId != null) {
            post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + postId));
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        File file = File.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath(targetLocation.toString())
                .fileType(multipartFile.getContentType())
                .fileSize(multipartFile.getSize())
                .post(post)
                .user(user)
                .build();

        File savedFile = fileRepository.save(file);

        return convertToResponseDto(savedFile);
    }

    public List<FileResponseDto> uploadMultipleFiles(List<MultipartFile> files, Long postId, String username) throws IOException {
        return files.stream()
                .map(file -> {
                    try {
                        return uploadFile(file, postId, username);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    public Resource downloadFile(Long fileId) throws MalformedURLException {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다: " + fileId));

        Path filePath = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("파일을 읽을 수 없습니다: " + fileId);
        }
    }

    public List<FileResponseDto> getFilesByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + postId));

        return fileRepository.findByPost(post)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<FileResponseDto> getFilesByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        return fileRepository.findByUser(user)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteFile(Long fileId, String username) throws IOException {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다: " + fileId));

        // 권한 확인 (파일 소유자 또는 ADMIN만 삭제 가능)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        if (!file.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("파일 삭제 권한이 없습니다.");
        }

        // 물리적 파일 삭제
        Path filePath = Paths.get(file.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // DB에서 파일 정보 삭제
        fileRepository.delete(file);
    }

    public void deleteFilesByPost(Long postId) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + postId));

        List<File> files = fileRepository.findByPost(post);
        for (File file : files) {
            deleteFile(file.getId(), file.getUser().getUsername());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("빈 파일은 업로드할 수 없습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new RuntimeException("파일명이 없습니다.");
        }

        String fileExtension = FilenameUtils.getExtension(originalFileName).toLowerCase();
        if (!Arrays.asList(allowedExtensions.split(",")).contains(fileExtension)) {
            throw new RuntimeException("허용되지 않는 파일 형식입니다: " + fileExtension);
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new RuntimeException("파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.");
        }
    }

    private String generateStoredFileName(String fileExtension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomString = RandomStringUtils.randomAlphanumeric(8);
        return timestamp + "_" + randomString + "." + fileExtension;
    }

    private FileResponseDto convertToResponseDto(File file) {
        return FileResponseDto.builder()
                .id(file.getId())
                .originalFileName(file.getOriginalFileName())
                .storedFileName(file.getStoredFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .createdAt(file.getCreatedAt())
                .build();
    }
}
