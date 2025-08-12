package com.blog.toy.config;

import com.blog.toy.domain.Category;
import com.blog.toy.domain.Comment;
import com.blog.toy.domain.Post;
import com.blog.toy.repository.CategoryRepository;
import com.blog.toy.repository.CommentRepository;
import com.blog.toy.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInsertScript implements CommandLineRunner {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // 기존 데이터가 있으면 초기화하지 않음
        if (postRepository.count() > 0) {
            System.out.println("✅ 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            System.out.println("현재 게시글 수: " + postRepository.count());
            System.out.println("현재 댓글 수: " + commentRepository.count());
            return;
        }

        System.out.println("카테고리가 적용된 새로운 더미 데이터를 생성합니다...");

        // 카테고리 생성
        Category javaCategory = Category.builder().name("JAVA").description("Java 프로그래밍 관련 게시글").displayOrder(1)
                .active(true).build();
        categoryRepository.save(javaCategory);

        Category springCategory = Category.builder().name("SPRING").description("Spring Framework 관련 게시글")
                .displayOrder(2).active(true).build();
        categoryRepository.save(springCategory);

        Category javascriptCategory = Category.builder().name("JAVASCRIPT").description("JavaScript 관련 게시글")
                .displayOrder(3).active(true).build();
        categoryRepository.save(javascriptCategory);

        Category reactCategory = Category.builder().name("REACT").description("React 관련 게시글").displayOrder(4)
                .active(true).build();
        categoryRepository.save(reactCategory);

        // 게시글 데이터 생성 (각 카테고리별로 5개씩, 총 20개)
        List<Post> posts = new ArrayList<>();

        String[] javaTopics = { "Java 기초 문법", "객체지향 프로그래밍", "컬렉션 프레임워크", "예외 처리", "제네릭스" };

        String[] springTopics = { "Spring Boot 시작하기", "의존성 주입", "AOP 프로그래밍", "트랜잭션 관리", "REST API 설계" };

        String[] javascriptTopics = { "JavaScript 기초", "ES6+ 문법", "비동기 프로그래밍", "Promise와 async/await", "DOM 조작" };

        String[] reactTopics = { "React 기초", "컴포넌트 설계", "상태 관리", "Props와 State", "Hooks 사용법" };

        String[] authors = { "김개발", "이코딩", "박웹개발", "최DB", "정DevOps" };

        // Java 카테고리 게시글 생성 (5개)
        for (int i = 1; i <= 5; i++) {
            Post post = Post.builder().title(javaTopics[i - 1] + " - Part " + i)
                    .content("이 글은 " + javaTopics[i - 1]
                            + "에 대한 내용입니다. Java와 Spring Boot를 사용한 개발, 객체지향 설계, 디자인 패턴 등 다양한 주제로 구성되어 있습니다.")
                    .author(authors[(i - 1) % authors.length]).category(javaCategory)
                    .viewCount((int) (Math.random() * 1000)).likeCount((int) (Math.random() * 100))
                    .createdAt(LocalDateTime.now().minusDays((int) (Math.random() * 30))).build();
            posts.add(post);
        }

        // Spring 카테고리 게시글 생성 (5개)
        for (int i = 1; i <= 5; i++) {
            Post post = Post.builder().title(springTopics[i - 1] + " - Part " + i)
                    .content("이 글은 " + springTopics[i - 1]
                            + "에 대한 내용입니다. Spring Boot와 JPA를 사용한 웹 개발, REST API 설계, 마이크로서비스 아키텍처 등 다양한 주제로 구성되어 있습니다.")
                    .author(authors[(i - 1) % authors.length]).category(springCategory)
                    .viewCount((int) (Math.random() * 1000)).likeCount((int) (Math.random() * 100))
                    .createdAt(LocalDateTime.now().minusDays((int) (Math.random() * 30))).build();
            posts.add(post);
        }

        // JavaScript 카테고리 게시글 생성 (5개)
        for (int i = 1; i <= 5; i++) {
            Post post = Post.builder().title(javascriptTopics[i - 1] + " - Part " + i)
                    .content("이 글은 " + javascriptTopics[i - 1]
                            + "에 대한 내용입니다. JavaScript와 React를 사용한 프론트엔드 개발, 웹 성능 최적화, 모던 웹 개발 등 다양한 주제로 구성되어 있습니다.")
                    .author(authors[(i - 1) % authors.length]).category(javascriptCategory)
                    .viewCount((int) (Math.random() * 1000)).likeCount((int) (Math.random() * 100))
                    .createdAt(LocalDateTime.now().minusDays((int) (Math.random() * 30))).build();
            posts.add(post);
        }

        // React 카테고리 게시글 생성 (5개)
        for (int i = 1; i <= 5; i++) {
            Post post = Post.builder().title(reactTopics[i - 1] + " - Part " + i)
                    .content("이 글은 " + reactTopics[i - 1]
                            + "에 대한 내용입니다. React와 TypeScript를 사용한 프론트엔드 개발, 상태 관리, 컴포넌트 설계 등 다양한 주제로 구성되어 있습니다.")
                    .author(authors[(i - 1) % authors.length]).category(reactCategory)
                    .viewCount((int) (Math.random() * 1000)).likeCount((int) (Math.random() * 100))
                    .createdAt(LocalDateTime.now().minusDays((int) (Math.random() * 30))).build();
            posts.add(post);
        }

        // 게시글 저장
        List<Post> savedPosts = postRepository.saveAll(posts);
        System.out.println("✅ " + savedPosts.size() + "개의 게시글이 생성되었습니다.");

        // 댓글 생성 (각 게시글당 2-3개)
        List<Comment> comments = new ArrayList<>();
        String[] commentContents = { "정말 유용한 정보네요! 감사합니다.", "이 내용으로 많은 도움이 되었어요.", "추가로 궁금한 점이 있는데요...", "좋은 글 감사합니다!",
                "이런 내용을 찾고 있었는데 정말 좋네요." };

        for (Post post : savedPosts) {
            int commentCount = 2 + (int) (Math.random() * 2); // 2-3개
            for (int j = 0; j < commentCount; j++) {
                Comment comment = Comment.builder()
                        .content(commentContents[(int) (Math.random() * commentContents.length)])
                        .author(authors[(int) (Math.random() * authors.length)]).post(post)
                        .likeCount((int) (Math.random() * 10)).dislikeCount((int) (Math.random() * 3))
                        .createdAt(LocalDateTime.now().minusDays((int) (Math.random() * 7))).build();
                comments.add(comment);
            }
        }

        commentRepository.saveAll(comments);
        System.out.println("✅ " + comments.size() + "개의 댓글이 생성되었습니다.");
        System.out.println("=== 카테고리별 게시글 수 ===");
        System.out.println("Java: " + javaCategory.getPosts().size() + "개");
        System.out.println("Spring: " + springCategory.getPosts().size() + "개");
        System.out.println("JavaScript: " + javascriptCategory.getPosts().size() + "개");
        System.out.println("React: " + reactCategory.getPosts().size() + "개");
        System.out.println("=== 데이터 생성 완료 ===");
    }
}
