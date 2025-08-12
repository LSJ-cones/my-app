package com.blog.toy.config;

import com.blog.toy.domain.Comment;
import com.blog.toy.domain.Post;
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

    @Override
    public void run(String... args) throws Exception {
        // 기존 데이터가 있으면 초기화하지 않음
        if (postRepository.count() > 0) {
            System.out.println("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            System.out.println("현재 게시글 수: " + postRepository.count());
            System.out.println("현재 댓글 수: " + commentRepository.count());
            return;
        }

        System.out.println("페이징 테스트를 위한 200개 더미 데이터를 생성합니다...");

        // 게시글 데이터 생성 (200개)
        List<Post> posts = new ArrayList<>();
        
        // 다양한 주제와 작성자로 200개의 게시글 생성
        String[] topics = {
            "Spring Boot", "JPA", "REST API", "데이터베이스", "Docker", "마이크로서비스", 
            "React", "보안", "테스트", "클라우드", "모니터링", "CI/CD", "성능", "코드리뷰", "애자일"
        };
        
        String[] authors = {
            "김개발", "이코딩", "박웹개발", "최DB", "정DevOps", "한아키텍트", "전풀스택", 
            "강보안", "윤테스터", "조클라우드", "임모니터", "백DevOps", "성튜너", "리뷰어", "애자일마스터"
        };
        
        String[] actions = {
            "시작하기", "완벽 가이드", "실전 예제", "최적화 기법", "배포 방법", "설계 패턴", 
            "연동 방법", "보안 기법", "테스트 방법", "클라우드 배포", "모니터링 시스템", 
            "자동화 파이프라인", "성능 튜닝", "리뷰 가이드", "개발 방법론"
        };

        for (int i = 1; i <= 200; i++) {
            String topic = topics[i % topics.length];
            String author = authors[i % authors.length];
            String action = actions[i % actions.length];
            
            String title = String.format("%s %s - Part %d", topic, action, i);
            String content = String.format(
                "이 글은 %s에 대한 %s 내용입니다. 이는 페이징 테스트를 위한 더미 데이터 #%d번째 게시글입니다. " +
                "실제 프로젝트에서는 이런 내용들이 포함될 것입니다. " +
                "Spring Boot와 JPA를 사용한 페이징 처리, REST API 설계, 데이터베이스 최적화 등 " +
                "다양한 주제로 구성되어 있습니다.", 
                topic, action, i
            );
            
            posts.add(createPost(title, content, author));
        }

        // 게시글 저장
        List<Post> savedPosts = postRepository.saveAll(posts);
        System.out.println("게시글 200개 생성 완료!");

        // 댓글 데이터 생성 (각 게시글당 3-8개의 댓글)
        String[] commentContents = {
            "좋은 글이네요!", "도움이 많이 되었습니다.", "추가 설명이 필요해요.", 
            "실습해보겠습니다.", "궁금한 점이 있어요.", "정말 유용한 정보입니다.", 
            "다음 글도 기대합니다.", "코드 예제가 더 있으면 좋겠어요.", 
            "실무에서 바로 적용할 수 있겠네요.", "오타가 있는 것 같습니다.",
            "더 자세한 설명 부탁드립니다.", "관련 자료도 있나요?", 
            "성능 측정 결과는 어떻게 되나요?", "다른 방법도 있나요?", 
            "실제 프로젝트에 적용해보겠습니다.", "이해가 잘 되네요!", 
            "추천합니다!", "꼭 읽어보세요!", "정말 좋은 내용입니다!"
        };
        
        String[] commentAuthors = {
            "댓글러1", "댓글러2", "댓글러3", "댓글러4", "댓글러5", "댓글러6", 
            "댓글러7", "댓글러8", "댓글러9", "댓글러10", "댓글러11", "댓글러12",
            "댓글러13", "댓글러14", "댓글러15", "댓글러16", "댓글러17", "댓글러18",
            "댓글러19", "댓글러20"
        };

        int totalComments = 0;
        for (Post post : savedPosts) {
            // 각 게시글당 3-8개의 댓글 생성
            int commentCount = 3 + (post.getId().intValue() % 6); // 3-8개
            List<Comment> comments = new ArrayList<>();
            
            for (int j = 0; j < commentCount; j++) {
                String content = commentContents[(int)(Math.random() * commentContents.length)];
                String author = commentAuthors[(int)(Math.random() * commentAuthors.length)];
                comments.add(createComment(post, content, author));
            }
            
            commentRepository.saveAll(comments);
            totalComments += commentCount;
        }

        System.out.println("페이징 테스트용 더미 데이터 생성 완료!");
        System.out.println("생성된 게시글 수: " + postRepository.count());
        System.out.println("생성된 댓글 수: " + commentRepository.count());
        System.out.println("페이징 테스트를 위해 다양한 페이지 크기로 테스트해보세요:");
        System.out.println("- 페이지 크기 10: 총 20페이지");
        System.out.println("- 페이지 크기 20: 총 10페이지");
        System.out.println("- 페이지 크기 50: 총 4페이지");
    }

    private Post createPost(String title, String content, String author) {
        LocalDateTime now = LocalDateTime.now();
        return Post.builder()
                .title(title)
                .content(content)
                .author(author)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private Comment createComment(Post post, String content, String author) {
        return Comment.builder()
                .post(post)
                .content(content)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
