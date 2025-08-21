package com.blog.toy.config;

import com.blog.toy.domain.Category;
import com.blog.toy.domain.Post;
import com.blog.toy.domain.User;
import com.blog.toy.repository.CategoryRepository;
import com.blog.toy.repository.PostRepository;
import com.blog.toy.repository.UserRepository;
import com.blog.toy.repository.CommentRepository;
import com.blog.toy.repository.PostReactionRepository;
import com.blog.toy.repository.CommentReactionRepository;
import com.blog.toy.repository.CommentReportRepository;
import com.blog.toy.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostReactionRepository postReactionRepository;

    @Autowired
    private CommentReactionRepository commentReactionRepository;

    @Autowired
    private CommentReportRepository commentReportRepository;

    @Autowired
    private FileRepository fileRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            System.out.println("=== 데이터 초기화 시작 ===");
            
            // 기존 데이터 삭제 (테스트용) - 외래키 제약조건을 고려한 순서
            System.out.println("🗑️ 기존 데이터 삭제 중...");
            
            // 외래키 제약조건을 고려하여 순서대로 삭제
            System.out.println("  - 댓글 반응 삭제 중...");
            commentReactionRepository.deleteAll();
            
            System.out.println("  - 댓글 신고 삭제 중...");
            commentReportRepository.deleteAll();
            
            System.out.println("  - 댓글 삭제 중...");
            commentRepository.deleteAll();
            
            System.out.println("  - 게시글 반응 삭제 중...");
            postReactionRepository.deleteAll();
            
            System.out.println("  - 파일 삭제 중...");
            fileRepository.deleteAll();
            
            System.out.println("  - 게시글 삭제 중...");
            postRepository.deleteAll();
            
            System.out.println("  - 카테고리 삭제 중...");
            categoryRepository.deleteAll();
            
            System.out.println("  - 사용자 삭제 중...");
            userRepository.deleteAll();
            
            System.out.println("✅ 기존 데이터 삭제 완료");
            
            // ADMIN 사용자 생성
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .name("관리자")
                    .role(User.Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(adminUser);
            System.out.println("✅ ADMIN 사용자가 생성되었습니다: admin / admin123");

            // 일반 사용자 생성
            User normalUser = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .name("일반사용자")
                    .role(User.Role.USER)
                    .enabled(true)
                    .build();
            userRepository.save(normalUser);
            System.out.println("✅ 일반 사용자가 생성되었습니다: user / user123");

            // 대분류 카테고리 생성
            Category devCategory = Category.builder()
                    .name("개발")
                    .description("소프트웨어 개발 관련")
                    .categoryType(Category.CategoryType.MAIN)
                    .displayOrder(1)
                    .active(true)
                    .build();
            categoryRepository.save(devCategory);

            Category infraCategory = Category.builder()
                    .name("인프라")
                    .description("서버 및 인프라 관련")
                    .categoryType(Category.CategoryType.MAIN)
                    .displayOrder(2)
                    .active(true)
                    .build();
            categoryRepository.save(infraCategory);

            Category dataCategory = Category.builder()
                    .name("데이터")
                    .description("데이터 분석 및 처리")
                    .categoryType(Category.CategoryType.MAIN)
                    .displayOrder(3)
                    .active(true)
                    .build();
            categoryRepository.save(dataCategory);

            Category etcCategory = Category.builder()
                    .name("기타")
                    .description("기타 기술 관련")
                    .categoryType(Category.CategoryType.MAIN)
                    .displayOrder(4)
                    .active(true)
                    .build();
            categoryRepository.save(etcCategory);

            // 소분류 카테고리 생성
            // 개발 하위
            Category javaCategory = Category.builder()
                    .name("Java")
                    .description("Java 개발")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(devCategory)
                    .displayOrder(1)
                    .active(true)
                    .build();
            categoryRepository.save(javaCategory);

            Category springCategory = Category.builder()
                    .name("Spring Boot")
                    .description("Spring Boot 프레임워크")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(devCategory)
                    .displayOrder(2)
                    .active(true)
                    .build();
            categoryRepository.save(springCategory);

            Category jsCategory = Category.builder()
                    .name("JavaScript")
                    .description("JavaScript 개발")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(devCategory)
                    .displayOrder(3)
                    .active(true)
                    .build();
            categoryRepository.save(jsCategory);

            Category pythonCategory = Category.builder()
                    .name("Python")
                    .description("Python 개발")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(devCategory)
                    .displayOrder(4)
                    .active(true)
                    .build();
            categoryRepository.save(pythonCategory);

            // 인프라 하위
            Category awsCategory = Category.builder()
                    .name("AWS")
                    .description("Amazon Web Services")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(infraCategory)
                    .displayOrder(1)
                    .active(true)
                    .build();
            categoryRepository.save(awsCategory);

            Category dockerCategory = Category.builder()
                    .name("Docker")
                    .description("컨테이너 기술")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(infraCategory)
                    .displayOrder(2)
                    .active(true)
                    .build();
            categoryRepository.save(dockerCategory);

            Category k8sCategory = Category.builder()
                    .name("Kubernetes")
                    .description("쿠버네티스")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(infraCategory)
                    .displayOrder(3)
                    .active(true)
                    .build();
            categoryRepository.save(k8sCategory);

            // 데이터 하위
            Category bigDataCategory = Category.builder()
                    .name("Big Data")
                    .description("빅데이터 처리")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(dataCategory)
                    .displayOrder(1)
                    .active(true)
                    .build();
            categoryRepository.save(bigDataCategory);

            Category aiCategory = Category.builder()
                    .name("AI/ML")
                    .description("인공지능 및 머신러닝")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(dataCategory)
                    .displayOrder(2)
                    .active(true)
                    .build();
            categoryRepository.save(aiCategory);

            Category dbCategory = Category.builder()
                    .name("Database")
                    .description("데이터베이스")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(dataCategory)
                    .displayOrder(3)
                    .active(true)
                    .build();
            categoryRepository.save(dbCategory);

            // 기타 하위
            Category trendsCategory = Category.builder()
                    .name("Tech Trends")
                    .description("기술 트렌드")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(etcCategory)
                    .displayOrder(1)
                    .active(true)
                    .build();
            categoryRepository.save(trendsCategory);

            Category devopsCategory = Category.builder()
                    .name("DevOps")
                    .description("개발 운영")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(etcCategory)
                    .displayOrder(2)
                    .active(true)
                    .build();
            categoryRepository.save(devopsCategory);

            Category webCategory = Category.builder()
                    .name("Web Development")
                    .description("웹 개발")
                    .categoryType(Category.CategoryType.SUB)
                    .parent(etcCategory)
                    .displayOrder(3)
                    .active(true)
                    .build();
            categoryRepository.save(webCategory);

            // 샘플 게시글 생성
            Post samplePost1 = Post.builder()
                    .title("Java 개발 환경 설정 가이드")
                    .content("Java 개발을 위한 환경 설정 방법을 안내합니다...")
                    .author("관리자")
                    .authorId(adminUser.getId())
                    .category(javaCategory)
                    .status(Post.PostStatus.PUBLISHED)
                    .viewCount(0)
                    .likeCount(0)
                    .dislikeCount(0)
                    .build();
            postRepository.save(samplePost1);

            Post samplePost2 = Post.builder()
                    .title("Spring Boot 시작하기")
                    .content("Spring Boot 프로젝트 생성 및 기본 설정...")
                    .author("관리자")
                    .authorId(adminUser.getId())
                    .category(springCategory)
                    .status(Post.PostStatus.PUBLISHED)
                    .viewCount(0)
                    .likeCount(0)
                    .dislikeCount(0)
                    .build();
            postRepository.save(samplePost2);

            Post samplePost3 = Post.builder()
                    .title("AWS EC2 인스턴스 생성")
                    .content("AWS EC2 인스턴스를 생성하고 설정하는 방법...")
                    .author("일반사용자")
                    .authorId(normalUser.getId())
                    .category(awsCategory)
                    .status(Post.PostStatus.PUBLISHED)
                    .viewCount(0)
                    .likeCount(0)
                    .dislikeCount(0)
                    .build();
            postRepository.save(samplePost3);

            System.out.println("✅ 계층형 카테고리와 샘플 게시글이 생성되었습니다.");
            System.out.println("=== 데이터 초기화 완료 ===");
        };
    }
}
