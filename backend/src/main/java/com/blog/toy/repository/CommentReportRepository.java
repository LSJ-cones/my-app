package com.blog.toy.repository;

import com.blog.toy.domain.Comment;
import com.blog.toy.domain.CommentReport;
import com.blog.toy.domain.ReportStatus;
import com.blog.toy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    
    // 특정 댓글의 모든 신고 조회
    List<CommentReport> findByComment(Comment comment);
    
    // 특정 상태의 신고 조회
    List<CommentReport> findByStatus(ReportStatus status);
    
    // 특정 사용자가 신고한 목록 조회
    List<CommentReport> findByReporter(User reporter);
    
    // 특정 댓글의 특정 사용자 신고 조회
    List<CommentReport> findByCommentAndReporter(Comment comment, User reporter);
    
    // 특정 댓글의 신고 수 조회
    long countByComment(Comment comment);
    
    // 특정 상태의 신고 수 조회
    long countByStatus(ReportStatus status);
}
