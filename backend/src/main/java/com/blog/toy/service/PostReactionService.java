package com.blog.toy.service;

import com.blog.toy.domain.*;
import com.blog.toy.dto.PostReactionDto;
import com.blog.toy.repository.PostReactionRepository;
import com.blog.toy.repository.PostRepository;
import com.blog.toy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostReactionService {

    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 게시글에 반응 추가/수정
    public PostReactionDto addReaction(Long postId, ReactionType type) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();

        // 기존 반응 확인
        Optional<PostReaction> existingReaction = postReactionRepository.findByPostAndUser(post, currentUser);

        PostReaction reaction;
        if (existingReaction.isPresent()) {
            // 기존 반응이 있으면 수정
            reaction = existingReaction.get();
            ReactionType oldType = reaction.getType();

            if (oldType == type) {
                // 같은 타입이면 반응 취소
                postReactionRepository.delete(reaction);
                updatePostReactionCounts(post, oldType, false);
                log.info("게시글 반응 취소: 게시글 ID {}, 사용자 {}, 타입 {}", postId, currentUser.getUsername(), type);
                return createReactionDto(post, currentUser, null, false);
            } else {
                // 다른 타입이면 변경
                reaction.setType(type);
                updatePostReactionCounts(post, oldType, false);
                updatePostReactionCounts(post, type, true);
                log.info("게시글 반응 변경: 게시글 ID {}, 사용자 {}, {} -> {}", postId, currentUser.getUsername(), oldType, type);
            }
        } else {
            // 새로운 반응 추가
            reaction = PostReaction.builder().post(post).user(currentUser).type(type).build();
            updatePostReactionCounts(post, type, true);
            log.info("새 게시글 반응 추가: 게시글 ID {}, 사용자 {}, 타입 {}", postId, currentUser.getUsername(), type);
        }

        PostReaction savedReaction = postReactionRepository.save(reaction);

        // 좋아요 알림 생성 (게시글 작성자에게)
        // Post 엔티티의 author는 String이므로 User 엔티티로 조회
        User postAuthor = userRepository.findByUsername(post.getAuthor()).orElse(null);
        if (type == ReactionType.LIKE && postAuthor != null && !currentUser.getId().equals(postAuthor.getId())) {
            System.out.println("🔔 게시글 좋아요 알림 생성 시도: 게시글 ID=" + postId + ", 게시글 작성자=" + post.getAuthor() + ", 반응 사용자=" + currentUser.getUsername());
            notificationService.createPostLikeNotification(savedReaction);
        }

        return createReactionDto(post, currentUser, savedReaction, true);
    }

    // 게시글 반응 취소
    public PostReactionDto removeReaction(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();

        Optional<PostReaction> existingReaction = postReactionRepository.findByPostAndUser(post, currentUser);

        if (existingReaction.isPresent()) {
            PostReaction reaction = existingReaction.get();
            ReactionType type = reaction.getType();

            postReactionRepository.delete(reaction);
            updatePostReactionCounts(post, type, false);

            log.info("게시글 반응 삭제: 게시글 ID {}, 사용자 {}, 타입 {}", postId, currentUser.getUsername(), type);
        }

        return createReactionDto(post, currentUser, null, false);
    }

    // 게시글 반응 조회
    public PostReactionDto getReaction(Long postId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
            log.info("게시글 조회 성공: ID {}", postId);

            // PostReaction 테이블 존재 여부 테스트
            try {
                long totalReactions = postReactionRepository.count();
                log.info("PostReaction 테이블 접근 성공, 총 반응 수: {}", totalReactions);
            } catch (Exception e) {
                log.error("PostReaction 테이블 접근 실패: {}", e.getMessage(), e);
                // 테이블이 없거나 접근할 수 없는 경우 기본 응답 반환
                return PostReactionDto.builder().id(null).postId(post.getId()).postTitle(post.getTitle()).userId(null)
                        .username("시스템").type(null).createdAt(null).likeCount(0L).dislikeCount(0L).userLiked(false)
                        .userDisliked(false).build();
            }

            try {
                User currentUser = getCurrentUser();
                log.info("현재 사용자 조회 성공: {}", currentUser.getUsername());

                Optional<PostReaction> reaction = postReactionRepository.findByPostAndUser(post, currentUser);
                log.info("사용자 반응 조회 완료: {}", reaction.isPresent());

                return createReactionDto(post, currentUser, reaction.orElse(null), reaction.isPresent());
            } catch (Exception e) {
                // 인증되지 않은 사용자의 경우 통계만 반환
                log.warn("인증되지 않은 사용자 접근: {}", e.getMessage());
                return createReactionDtoForAnonymous(post);
            }
        } catch (Exception e) {
            log.error("게시글 반응 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 게시글의 모든 반응 조회
    public List<PostReactionDto> getPostReactions(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        List<PostReaction> reactions = postReactionRepository.findByPost(post);

        return reactions.stream()
                .map(reaction -> PostReactionDto.builder().id(reaction.getId()).postId(reaction.getPost().getId())
                        .postTitle(reaction.getPost().getTitle()).userId(reaction.getUser().getId())
                        .username(reaction.getUser().getUsername()).type(reaction.getType())
                        .createdAt(reaction.getCreatedAt()).build())
                .collect(Collectors.toList());
    }

    // 사용자의 모든 반응 조회
    public List<PostReactionDto> getUserReactions() {
        User currentUser = getCurrentUser();
        List<PostReaction> reactions = postReactionRepository.findByUser(currentUser);

        return reactions.stream()
                .map(reaction -> PostReactionDto.builder().id(reaction.getId()).postId(reaction.getPost().getId())
                        .postTitle(reaction.getPost().getTitle()).userId(reaction.getUser().getId())
                        .username(reaction.getUser().getUsername()).type(reaction.getType())
                        .createdAt(reaction.getCreatedAt()).build())
                .collect(Collectors.toList());
    }

    // 게시글 반응 통계 조회
    public PostReactionDto getReactionStats(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();
        Optional<PostReaction> userReaction = postReactionRepository.findByPostAndUser(post, currentUser);

        return createReactionDto(post, currentUser, userReaction.orElse(null), userReaction.isPresent());
    }

    // 게시글 반응 개수 업데이트
    private void updatePostReactionCounts(Post post, ReactionType type, boolean increment) {
        if (type == ReactionType.LIKE) {
            if (increment) {
                post.incrementLikeCount();
            } else {
                post.decrementLikeCount();
            }
        } else if (type == ReactionType.DISLIKE) {
            if (increment) {
                post.incrementDislikeCount();
            } else {
                post.decrementDislikeCount();
            }
        }
        postRepository.save(post);
    }

    // 반응 DTO 생성
    private PostReactionDto createReactionDto(Post post, User user, PostReaction reaction, boolean hasReaction) {
        try {
            log.info("반응 DTO 생성 시작: 게시글 ID {}, 사용자 {}", post.getId(), user.getUsername());

            // 반응 통계 조회 (더 안전한 방법)
            Long likeCount = 0L;
            Long dislikeCount = 0L;
            try {
                Object[] stats = postReactionRepository.getReactionStats(post);
                likeCount = stats != null ? (Long) stats[0] : 0L;
                dislikeCount = stats != null ? (Long) stats[1] : 0L;
                log.info("반응 통계 조회 완료: 좋아요 {}, 싫어요 {}", likeCount, dislikeCount);
            } catch (Exception e) {
                log.warn("반응 통계 조회 실패, 기본값 사용: {}", e.getMessage());
                // 대안으로 개별 카운트 사용
                likeCount = (long) postReactionRepository.countByPostAndType(post, ReactionType.LIKE);
                dislikeCount = (long) postReactionRepository.countByPostAndType(post, ReactionType.DISLIKE);
            }

            // 사용자의 반응 상태 확인
            boolean userLiked = false;
            boolean userDisliked = false;
            try {
                userLiked = postReactionRepository.existsByPostAndUserAndType(post, user, ReactionType.LIKE);
                userDisliked = postReactionRepository.existsByPostAndUserAndType(post, user, ReactionType.DISLIKE);
                log.info("사용자 반응 상태: 좋아요 {}, 싫어요 {}", userLiked, userDisliked);
            } catch (Exception e) {
                log.warn("사용자 반응 상태 확인 실패: {}", e.getMessage());
            }

            PostReactionDto dto = PostReactionDto.builder().id(reaction != null ? reaction.getId() : null)
                    .postId(post.getId()).postTitle(post.getTitle()).userId(user.getId()).username(user.getUsername())
                    .type(reaction != null ? reaction.getType() : null)
                    .createdAt(reaction != null ? reaction.getCreatedAt() : null).likeCount(likeCount)
                    .dislikeCount(dislikeCount).userLiked(userLiked).userDisliked(userDisliked).build();

            log.info("반응 DTO 생성 완료");
            return dto;
        } catch (Exception e) {
            log.error("반응 DTO 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 익명 사용자를 위한 반응 DTO 생성
    private PostReactionDto createReactionDtoForAnonymous(Post post) {
        try {
            log.info("익명 사용자용 반응 DTO 생성 시작: 게시글 ID {}", post.getId());

            // 반응 통계 조회 (더 안전한 방법)
            Long likeCount = 0L;
            Long dislikeCount = 0L;
            try {
                Object[] stats = postReactionRepository.getReactionStats(post);
                likeCount = stats != null ? (Long) stats[0] : 0L;
                dislikeCount = stats != null ? (Long) stats[1] : 0L;
                log.info("익명 사용자 반응 통계 조회 완료: 좋아요 {}, 싫어요 {}", likeCount, dislikeCount);
            } catch (Exception e) {
                log.warn("익명 사용자 반응 통계 조회 실패, 기본값 사용: {}", e.getMessage());
                // 대안으로 개별 카운트 사용
                likeCount = (long) postReactionRepository.countByPostAndType(post, ReactionType.LIKE);
                dislikeCount = (long) postReactionRepository.countByPostAndType(post, ReactionType.DISLIKE);
            }

            PostReactionDto dto = PostReactionDto.builder().id(null).postId(post.getId()).postTitle(post.getTitle())
                    .userId(null).username("익명 사용자").type(null).createdAt(null).likeCount(likeCount)
                    .dislikeCount(dislikeCount).userLiked(false).userDisliked(false).build();

            log.info("익명 사용자용 반응 DTO 생성 완료");
            return dto;
        } catch (Exception e) {
            log.error("익명 사용자용 반응 DTO 생성 중 오류 발생: {}", e.getMessage(), e);
            // 최소한의 정보라도 반환
            return PostReactionDto.builder().id(null).postId(post.getId()).postTitle(post.getTitle()).userId(null)
                    .username("익명 사용자").type(null).createdAt(null).likeCount(0L).dislikeCount(0L).userLiked(false)
                    .userDisliked(false).build();
        }
    }

    // 현재 사용자 조회
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("인증 정보: {}", authentication != null ? authentication.getName() : "null");

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getName())) {
                // 임시로 admin 사용자 반환 (테스트용)
                Optional<User> adminUser = userRepository.findByUsername("admin");
                if (adminUser.isPresent()) {
                    log.info("기존 admin 사용자 사용");
                    return adminUser.get();
                } else {
                    // admin 사용자가 없으면 생성
                    log.info("새로운 admin 사용자 생성");
                    User newAdmin = User.builder().username("admin").email("admin@example.com")
                            .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // "password"
                            .name("관리자").role(User.Role.ADMIN).build();
                    User savedAdmin = userRepository.save(newAdmin);
                    log.info("admin 사용자 생성 완료: ID {}", savedAdmin.getId());
                    return savedAdmin;
                }
            }

            String username = authentication.getName();
            log.info("인증된 사용자 조회: {}", username);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));
        } catch (Exception e) {
            log.error("현재 사용자 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}
