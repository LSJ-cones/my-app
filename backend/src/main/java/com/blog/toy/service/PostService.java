package com.blog.toy.service;

import com.blog.toy.domain.Category;
import com.blog.toy.domain.Post;
import com.blog.toy.domain.Tag;
import com.blog.toy.dto.CommentResponseDto;
import com.blog.toy.dto.PageRequestDto;
import com.blog.toy.dto.PageResponseDto;
import com.blog.toy.dto.PostRequestDto;
import com.blog.toy.dto.PostResponseDto;
import com.blog.toy.dto.category.CategoryResponseDto;
import com.blog.toy.dto.file.FileResponseDto;
import com.blog.toy.dto.tag.TagResponseDto;
import com.blog.toy.repository.CategoryRepository;
import com.blog.toy.repository.PostRepository;
import com.blog.toy.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    // 전체 게시글 조회
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    // 개별 게시글 조회
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    // 게시글 생성
    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .author(postRequestDto.getAuthor())
                .status(postRequestDto.getStatus())
                .viewCount(0)
                .build();

        // 카테고리 설정
        if (postRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(postRequestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + postRequestDto.getCategoryId()));
            post.setCategory(category);
        }

        // 태그 설정
        if (postRequestDto.getTagNames() != null && !postRequestDto.getTagNames().isEmpty()) {
            List<Tag> tags = tagService.findOrCreateTags(postRequestDto.getTagNames());
            post.setTags(tags);
        }

        Post savedPost = postRepository.save(post);
        return convertToResponseDto(savedPost);
    }

    // 게시글 수정
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + id));

        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setAuthor(postRequestDto.getAuthor());
        post.setStatus(postRequestDto.getStatus());

        // 카테고리 설정
        if (postRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(postRequestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + postRequestDto.getCategoryId()));
            post.setCategory(category);
        } else {
            post.setCategory(null);
        }

        // 태그 설정
        if (postRequestDto.getTagNames() != null) {
            post.getTags().clear();
            if (!postRequestDto.getTagNames().isEmpty()) {
                List<Tag> tags = tagService.findOrCreateTags(postRequestDto.getTagNames());
                post.setTags(tags);
            }
        }

        Post updatedPost = postRepository.save(post);
        return convertToResponseDto(updatedPost);
    }

    // 게시글 삭제
    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    // 게시글 검색
    public List<Post> search(String keyword) {
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }
    
    // 페이징을 통한 게시글 조회
    public PageResponseDto<PostResponseDto> findAllWithPaging(PageRequestDto pageRequestDto) {
        Page<Post> postPage = postRepository.findAll(pageRequestDto.toPageable());
        
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        
        return new PageResponseDto<>(postResponseDtoPage);
    }
    
    // 페이징을 통한 게시글 검색
    public PageResponseDto<PostResponseDto> searchWithPaging(String keyword, PageRequestDto pageRequestDto) {
        Page<Post> postPage = postRepository.findByTitleContainingOrContentContaining(
            keyword, keyword, pageRequestDto.toPageable());
        
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        
        return new PageResponseDto<>(postResponseDtoPage);
    }

    // 카테고리별 게시글 조회
    public PageResponseDto<PostResponseDto> findByCategory(Long categoryId, PageRequestDto pageRequestDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + categoryId));
        
        Page<Post> postPage = postRepository.findByCategory(category, pageRequestDto.toPageable());
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        
        return new PageResponseDto<>(postResponseDtoPage);
    }

    // 태그별 게시글 조회
    public PageResponseDto<PostResponseDto> findByTags(List<String> tagNames, PageRequestDto pageRequestDto) {
        List<Tag> tags = tagRepository.findByNamesIn(tagNames);
        if (tags.isEmpty()) {
            return new PageResponseDto<>(Page.empty(pageRequestDto.toPageable()));
        }
        
        Page<Post> postPage = postRepository.findByTags(tags, pageRequestDto.toPageable());
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        
        return new PageResponseDto<>(postResponseDtoPage);
    }

    // 상태별 게시글 조회
    public PageResponseDto<PostResponseDto> findByStatus(Post.PostStatus status, PageRequestDto pageRequestDto) {
        Page<Post> postPage = postRepository.findByStatus(status, pageRequestDto.toPageable());
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        
        return new PageResponseDto<>(postResponseDtoPage);
    }

    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + id));
        post.incrementViewCount();
        postRepository.save(post);
    }
   
    // 게시글과 해당 게시글의 댓글을 포함한 DTO 반환
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

        PostResponseDto responseDto = convertToResponseDto(post);
        responseDto.setComments(commentDtos);
        return responseDto;
    }

    // Post 엔티티를 PostResponseDto로 변환
    private PostResponseDto convertToResponseDto(Post post) {
        CategoryResponseDto categoryDto = null;
        if (post.getCategory() != null) {
            categoryDto = CategoryResponseDto.builder()
                    .id(post.getCategory().getId())
                    .name(post.getCategory().getName())
                    .description(post.getCategory().getDescription())
                    .build();
        }

                       List<TagResponseDto> tagDtos = post.getTags().stream()
                       .map(tag -> TagResponseDto.builder()
                               .id(tag.getId())
                               .name(tag.getName())
                               .description(tag.getDescription())
                               .build())
                       .collect(Collectors.toList());

               List<FileResponseDto> fileDtos = post.getFiles().stream()
                       .map(file -> FileResponseDto.builder()
                               .id(file.getId())
                               .originalFileName(file.getOriginalFileName())
                               .storedFileName(file.getStoredFileName())
                               .fileType(file.getFileType())
                               .fileSize(file.getFileSize())
                               .createdAt(file.getCreatedAt())
                               .build())
                       .collect(Collectors.toList());

               return PostResponseDto.builder()
                       .id(post.getId())
                       .title(post.getTitle())
                       .content(post.getContent())
                       .author(post.getAuthor())
                       .status(post.getStatus())
                       .viewCount(post.getViewCount())
                       .category(categoryDto)
                       .tags(tagDtos)
                       .files(fileDtos)
                       .createdAt(post.getCreatedAt())
                       .updatedAt(post.getUpdatedAt())
                       .build();
    }
}
