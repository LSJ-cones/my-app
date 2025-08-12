package com.blog.toy.config;

import com.blog.toy.domain.Category;
import com.blog.toy.domain.Post;
import com.blog.toy.domain.User;
import com.blog.toy.repository.CategoryRepository;
import com.blog.toy.repository.PostRepository;
import com.blog.toy.repository.UserRepository;
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

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            System.out.println("=== 데이터 초기화 시작 ===");
            
            // ADMIN 사용자 생성
            if (!userRepository.existsByUsername("admin")) {
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
            } else {
                System.out.println("ℹ️ ADMIN 사용자가 이미 존재합니다.");
            }

            // 일반 사용자 생성
            if (!userRepository.existsByUsername("user")) {
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
            } else {
                System.out.println("ℹ️ 일반 사용자가 이미 존재합니다.");
            }

            // 기본 카테고리 생성
            if (categoryRepository.count() == 0) {
                Category springCategory = Category.builder()
                        .name("Spring Boot")
                        .description("Spring Boot 관련 게시글")
                        .displayOrder(1)
                        .active(true)
                        .build();
                categoryRepository.save(springCategory);

                Category javaCategory = Category.builder()
                        .name("Java")
                        .description("Java 프로그래밍 관련 게시글")
                        .displayOrder(2)
                        .active(true)
                        .build();
                categoryRepository.save(javaCategory);

                Category webCategory = Category.builder()
                        .name("Web Development")
                        .description("웹 개발 관련 게시글")
                        .displayOrder(3)
                        .active(true)
                        .build();
                categoryRepository.save(webCategory);

                System.out.println("✅ 기본 카테고리가 생성되었습니다.");
            } else {
                System.out.println("ℹ️ 카테고리가 이미 존재합니다.");
            }

            System.out.println("=== 데이터 초기화 완료 ===");
        };
    }
}
