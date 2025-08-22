package com.blog.toy.service;

import com.blog.toy.domain.*;
import com.blog.toy.domain.User.Role;
import com.blog.toy.dto.*;
import com.blog.toy.repository.CommentRepository;
import com.blog.toy.repository.CommentReactionRepository;
import com.blog.toy.repository.CommentReportRepository;
import com.blog.toy.repository.PostRepository;
import com.blog.toy.repository.UserRepository;
import com.blog.toy.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final CommentReportRepository commentReportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 댓글 조회 (대댓글 포함)
    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndStatusAndParentIsNull(postId, CommentStatus.ACTIVE);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 댓글 조회 (페이징)
    public PageResponseDto<CommentResponseDto> getCommentsByPostIdWithPaging(Long postId, PageRequestDto pageRequestDto) {
        Page<Comment> commentPage = commentRepository.findByPostIdAndParentIsNull(postId, pageRequestDto.toPageable());
        
        Page<CommentResponseDto> commentResponseDtoPage = commentPage.map(this::convertToDto);
        
        return new PageResponseDto<>(commentResponseDtoPage);
    }

    // 댓글 생성
    public CommentResponseDto createComment(CommentRequestDto requestDto) {
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();
        
        // 멘션 처리
        String content = requestDto.getContent();
        if (requestDto.getMentionUsername() != null && !requestDto.getMentionUsername().trim().isEmpty()) {
            content = "@" + requestDto.getMentionUsername() + " " + content;
        }
        
        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .user(currentUser)
                .author(currentUser.getUsername())
                .status(CommentStatus.ACTIVE)
                .likeCount(0)
                .dislikeCount(0)
                .build();

        // 대댓글인 경우
        if (requestDto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));
            comment.setParent(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        
        // 알림 생성
        if (requestDto.getParentId() != null) {
            // 대댓글인 경우
            Comment parentComment = commentRepository.findById(requestDto.getParentId()).orElse(null);
            if (parentComment != null) {
                System.out.println("🔔 대댓글 알림 생성 시도: 댓글 ID=" + savedComment.getId() + ", 부모 댓글 작성자=" + parentComment.getAuthor());
                notificationService.createReplyNotification(savedComment, parentComment);
            }
        } else {
            // 일반 댓글인 경우
            System.out.println("🔔 댓글 알림 생성 시도: 댓글 ID=" + savedComment.getId() + ", 게시글 작성자=" + post.getAuthor());
            notificationService.createCommentNotification(savedComment, post);
        }
        
        return convertToDto(savedComment);
    }

    // 댓글 수정
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();
        if (!comment.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(requestDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        
        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    // 댓글 삭제 (소프트 삭제)
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();
        if (!comment.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.markAsDeleted();
        commentRepository.save(comment);
    }

    // 댓글 좋아요/싫어요
    public CommentResponseDto reactToComment(CommentReactionDto reactionDto) {
        Comment comment = commentRepository.findById(reactionDto.getCommentId())
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();

        // 기존 반응 확인
        Optional<CommentReaction> existingReaction = commentReactionRepository.findByCommentAndUser(comment, currentUser);

        if (existingReaction.isPresent()) {
            CommentReaction reaction = existingReaction.get();
            
            // 같은 반응이면 취소
            if (reaction.getType() == reactionDto.getType()) {
                if (reactionDto.getType() == ReactionType.LIKE) {
                    comment.decrementLikeCount();
                } else {
                    comment.decrementDislikeCount();
                }
                commentReactionRepository.delete(reaction);
            } else {
                // 다른 반응이면 변경
                if (reaction.getType() == ReactionType.LIKE) {
                    comment.decrementLikeCount();
                } else {
                    comment.decrementDislikeCount();
                }
                
                if (reactionDto.getType() == ReactionType.LIKE) {
                    comment.incrementLikeCount();
                } else {
                    comment.incrementDislikeCount();
                }
                
                reaction.setType(reactionDto.getType());
                commentReactionRepository.save(reaction);
            }
        } else {
            // 새로운 반응 추가
            System.out.println("🔍 CommentService - 새로운 반응 추가");
            System.out.println("🔍 CommentService - reactionDto.getType(): " + reactionDto.getType());
            System.out.println("🔍 CommentService - reactionDto: " + reactionDto);
            
            CommentReaction newReaction = CommentReaction.builder()
                    .comment(comment)
                    .user(currentUser)
                    .type(reactionDto.getType())
                    .build();
            
            System.out.println("🔍 CommentService - 생성된 newReaction.getType(): " + newReaction.getType());
            
            if (reactionDto.getType() == ReactionType.LIKE) {
                comment.incrementLikeCount();
            } else {
                comment.incrementDislikeCount();
            }
            
            System.out.println("🔍 CommentService - 저장 전 newReaction: " + newReaction);
            commentReactionRepository.save(newReaction);
            
            // 새로운 반응에 대한 알림 생성
            notificationService.createLikeNotification(newReaction);
        }

        commentRepository.save(comment);
        return convertToDto(comment);
    }

    // 댓글 신고
    public void reportComment(CommentReportDto reportDto) {
        Comment comment = commentRepository.findById(reportDto.getCommentId())
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();

        // 이미 신고했는지 확인
        List<CommentReport> existingReports = commentReportRepository.findByCommentAndReporter(comment, currentUser);
        if (!existingReports.isEmpty()) {
            throw new RuntimeException("이미 신고한 댓글입니다.");
        }

        CommentReport report = CommentReport.builder()
                .comment(comment)
                .reporter(currentUser)
                .reason(reportDto.getReason())
                .description(reportDto.getDescription())
                .status(ReportStatus.PENDING)
                .build();

        commentReportRepository.save(report);
    }

    // 대댓글 조회
    public List<CommentResponseDto> getRepliesByCommentId(Long commentId) {
        List<Comment> replies = commentRepository.findByParentId(commentId);
        return replies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 사용자의 댓글 조회
    public PageResponseDto<CommentResponseDto> getUserComments(Long userId, PageRequestDto pageRequestDto) {
        Page<Comment> commentPage = commentRepository.findByUserId(userId, pageRequestDto.toPageable());
        Page<CommentResponseDto> commentResponseDtoPage = commentPage.map(this::convertToDto);
        return new PageResponseDto<>(commentResponseDtoPage);
    }

    // 신고된 댓글 조회 (관리자용)
    public PageResponseDto<CommentResponseDto> getReportedComments(PageRequestDto pageRequestDto) {
        Page<Comment> commentPage = commentRepository.findByStatus(CommentStatus.REPORTED, pageRequestDto.toPageable());
        Page<CommentResponseDto> commentResponseDtoPage = commentPage.map(this::convertToDto);
        return new PageResponseDto<>(commentResponseDtoPage);
    }

    // 신고 처리 (관리자용)
    public void handleReport(Long reportId, ReportStatus status) {
        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));

        report.setStatus(status);
        commentReportRepository.save(report);

        if (status == ReportStatus.RESOLVED) {
            Comment comment = report.getComment();
            comment.markAsReported();
            commentRepository.save(comment);
        }
    }



    // DTO 변환 메서드
    private CommentResponseDto convertToDto(Comment comment) {
        User currentUser = getCurrentUser();
        
        // 현재 사용자의 반응 확인
        Boolean isLiked = null;
        Boolean isDisliked = null;
        
        if (currentUser != null) {
            try {
                Optional<CommentReaction> reaction = commentReactionRepository.findByCommentAndUser(comment, currentUser);
                if (reaction.isPresent()) {
                    isLiked = reaction.get().getType() == ReactionType.LIKE;
                    isDisliked = reaction.get().getType() == ReactionType.DISLIKE;
                }
            } catch (Exception e) {
                // 반응 조회 중 오류가 발생하면 무시
                isLiked = null;
                isDisliked = null;
            }
        }

        // 대댓글 변환
        List<CommentResponseDto> replies = comment.getReplies() != null ? 
                comment.getReplies().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()) : 
                new ArrayList<>();

        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .status(comment.getStatus())
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(replies)
                .isLiked(isLiked)
                .isDisliked(isDisliked)
                .authorName(comment.getUser() != null ? comment.getUser().getName() : null)
                .authorEmail(comment.getUser() != null ? comment.getUser().getEmail() : null)
                .build();
    }

    // 현재 사용자 조회
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            // 임시로 admin 사용자 반환 (테스트용)
            return userRepository.findByUsername("admin")
                    .orElse(null);
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElse(null);
    }
}
