package com.blog.toy.service;

import com.blog.toy.domain.Category;
import com.blog.toy.domain.Post;
import com.blog.toy.domain.PostReaction;
import com.blog.toy.domain.ReactionType;
import com.blog.toy.domain.Tag;
import com.blog.toy.domain.User;
import com.blog.toy.dto.CommentResponseDto;
import com.blog.toy.dto.PageRequestDto;
import com.blog.toy.dto.PageResponseDto;
import com.blog.toy.dto.PostReactionDto;
import com.blog.toy.dto.PostRequestDto;
import com.blog.toy.dto.PostResponseDto;
import com.blog.toy.dto.SearchRequestDto;
import com.blog.toy.dto.category.CategoryResponseDto;
import com.blog.toy.dto.file.FileResponseDto;
import com.blog.toy.dto.tag.TagResponseDto;
import com.blog.toy.repository.CategoryRepository;
import com.blog.toy.repository.PostReactionRepository;
import com.blog.toy.repository.PostRepository;
import com.blog.toy.repository.TagRepository;
import com.blog.toy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

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

    @Autowired
    private PostReactionRepository postReactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

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

    // 카테고리명으로 게시글 조회
    public PageResponseDto<PostResponseDto> findByCategoryName(String categoryName, PageRequestDto pageRequestDto) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + categoryName));
        
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

    // === 고급 검색 기능 ===
    
    // 고급 검색 (복합 조건)
    public PageResponseDto<PostResponseDto> advancedSearch(SearchRequestDto searchRequestDto) {
        // Pageable 생성
        Pageable pageable = createPageable(searchRequestDto);
        
        // 검색 조건에 따른 분기 처리
        Page<Post> postPage;
        
        if (StringUtils.hasText(searchRequestDto.getKeyword())) {
            // 키워드가 있는 경우
            if (searchRequestDto.getCategoryId() != null && searchRequestDto.getTagNames() != null && !searchRequestDto.getTagNames().isEmpty()) {
                // 키워드 + 카테고리 + 태그
                postPage = postRepository.findByAdvancedSearch(
                    searchRequestDto.getKeyword(),
                    searchRequestDto.getCategoryId(),
                    searchRequestDto.getTagNames(),
                    searchRequestDto.getStatus(),
                    pageable
                );
            } else if (searchRequestDto.getCategoryId() != null) {
                // 키워드 + 카테고리
                postPage = postRepository.findByKeywordAndCategory(
                    searchRequestDto.getKeyword(),
                    searchRequestDto.getCategoryId(),
                    pageable
                );
            } else if (searchRequestDto.getTagNames() != null && !searchRequestDto.getTagNames().isEmpty()) {
                // 키워드 + 태그
                postPage = postRepository.findByKeywordAndTags(
                    searchRequestDto.getKeyword(),
                    searchRequestDto.getTagNames(),
                    pageable
                );
            } else if (searchRequestDto.getStatus() != null) {
                // 키워드 + 상태
                postPage = postRepository.findByKeywordAndStatus(
                    searchRequestDto.getKeyword(),
                    searchRequestDto.getStatus(),
                    pageable
                );
            } else {
                // 키워드만
                postPage = postRepository.findByKeyword(searchRequestDto.getKeyword(), pageable);
            }
        } else {
            // 키워드가 없는 경우
            if (searchRequestDto.getCategoryId() != null && searchRequestDto.getTagNames() != null && !searchRequestDto.getTagNames().isEmpty()) {
                // 카테고리 + 태그 - 복합 검색 사용
                postPage = postRepository.findByAdvancedSearch(
                    null,
                    searchRequestDto.getCategoryId(),
                    searchRequestDto.getTagNames(),
                    searchRequestDto.getStatus(),
                    pageable
                );
            } else if (searchRequestDto.getCategoryId() != null) {
                // 카테고리만
                postPage = postRepository.findByCategoryId(searchRequestDto.getCategoryId(), pageable);
            } else if (searchRequestDto.getTagNames() != null && !searchRequestDto.getTagNames().isEmpty()) {
                // 태그만
                postPage = postRepository.findByTagNames(searchRequestDto.getTagNames(), pageable);
            } else if (searchRequestDto.getStatus() != null) {
                // 상태만
                postPage = postRepository.findByStatus(searchRequestDto.getStatus(), pageable);
            } else {
                // 조건 없음 - 전체 조회
                postPage = postRepository.findAll(pageable);
            }
        }
        
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        return new PageResponseDto<>(postResponseDtoPage);
    }
    
    // 인기 게시글 조회 (조회수 높은 순)
    public PageResponseDto<PostResponseDto> findPopularPosts(PageRequestDto pageRequestDto) {
        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        Page<Post> postPage = postRepository.findPopularPosts(thirtyDaysAgo, pageRequestDto.toPageable());
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        return new PageResponseDto<>(postResponseDtoPage);
    }
    
    // 최근 게시글 조회 (최근 7일)
    public PageResponseDto<PostResponseDto> findRecentPosts(PageRequestDto pageRequestDto) {
        java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7);
        Page<Post> postPage = postRepository.findRecentPosts(sevenDaysAgo, pageRequestDto.toPageable());
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        return new PageResponseDto<>(postResponseDtoPage);
    }
    
    // 댓글 많은 순으로 정렬
    public PageResponseDto<PostResponseDto> findPostsByCommentCount(PageRequestDto pageRequestDto) {
        Page<Post> postPage = postRepository.findAllOrderByCommentCountDesc(pageRequestDto.toPageable());
        Page<PostResponseDto> postResponseDtoPage = postPage.map(this::convertToResponseDto);
        return new PageResponseDto<>(postResponseDtoPage);
    }

    // 게시글 반응 (좋아요/싫어요)
    public PostResponseDto reactToPost(PostReactionDto reactionDto) {
        Post post = postRepository.findById(reactionDto.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();

        // 기존 반응 확인
        Optional<PostReaction> existingReaction = postReactionRepository.findByPostAndUser(post, currentUser);

        if (existingReaction.isPresent()) {
            PostReaction reaction = existingReaction.get();
            
            // 같은 반응이면 취소
            if (reaction.getType() == reactionDto.getType()) {
                if (reactionDto.getType() == ReactionType.LIKE) {
                    post.decrementLikeCount();
                } else {
                    post.decrementDislikeCount();
                }
                postReactionRepository.delete(reaction);
            } else {
                // 다른 반응이면 변경
                if (reaction.getType() == ReactionType.LIKE) {
                    post.decrementLikeCount();
                } else {
                    post.decrementDislikeCount();
                }
                
                if (reactionDto.getType() == ReactionType.LIKE) {
                    post.incrementLikeCount();
                } else {
                    post.incrementDislikeCount();
                }
                
                reaction.setType(reactionDto.getType());
                postReactionRepository.save(reaction);
            }
        } else {
            // 새로운 반응 추가
            PostReaction newReaction = PostReaction.builder()
                    .post(post)
                    .user(currentUser)
                    .type(reactionDto.getType())
                    .build();
            
            if (reactionDto.getType() == ReactionType.LIKE) {
                post.incrementLikeCount();
            } else {
                post.incrementDislikeCount();
            }
            
            postReactionRepository.save(newReaction);
            
            // 새로운 반응에 대한 알림 생성
            notificationService.createPostLikeNotification(newReaction);
        }

        postRepository.save(post);
        return convertToResponseDto(post);
    }

    // 현재 사용자 조회
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            // 임시로 admin 사용자 반환 (테스트용)
            return userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
    
    // Pageable 생성 헬퍼 메서드
    private Pageable createPageable(SearchRequestDto searchRequestDto) {
        int page = searchRequestDto.getPage() != null ? searchRequestDto.getPage() : 0;
        int size = searchRequestDto.getSize() != null ? searchRequestDto.getSize() : 10;
        
        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(searchRequestDto.getSortBy())) {
            Sort.Direction direction = "desc".equalsIgnoreCase(searchRequestDto.getSortDirection()) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            switch (searchRequestDto.getSortBy().toLowerCase()) {
                case "title":
                    sort = Sort.by(direction, "title");
                    break;
                case "createdat":
                    sort = Sort.by(direction, "createdAt");
                    break;
                case "viewcount":
                    sort = Sort.by(direction, "viewCount");
                    break;
                case "commentcount":
                    sort = Sort.by(direction, "comments.size");
                    break;
                default:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
            }
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        return PageRequest.of(page, size, sort);
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

                       List<TagResponseDto> tagDtos = (post.getTags() != null) ? post.getTags().stream()
                       .map(tag -> TagResponseDto.builder()
                               .id(tag.getId())
                               .name(tag.getName())
                               .description(tag.getDescription())
                               .build())
                       .collect(Collectors.toList()) : new ArrayList<>();

               List<FileResponseDto> fileDtos = (post.getFiles() != null) ? post.getFiles().stream()
                       .map(file -> FileResponseDto.builder()
                               .id(file.getId())
                               .originalFileName(file.getOriginalFileName())
                               .storedFileName(file.getStoredFileName())
                               .fileType(file.getFileType())
                               .fileSize(file.getFileSize())
                               .createdAt(file.getCreatedAt())
                               .build())
                       .collect(Collectors.toList()) : new ArrayList<>();

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
