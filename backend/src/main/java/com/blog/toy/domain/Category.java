package com.blog.toy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active")
    private boolean active = true;

    // 계층 구조 지원
    @Column(name = "category_type")
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType = CategoryType.SUB; // 기본값은 소분류

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent; // 상위 카테고리 (대분류)

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>(); // 하위 카테고리들 (소분류들)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 게시글과의 관계 (1:N)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    // 카테고리 타입 enum
    public enum CategoryType {
        MAIN,   // 대분류
        SUB     // 소분류
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 대분류인지 확인
    public boolean isMainCategory() {
        return this.categoryType == CategoryType.MAIN;
    }

    // 소분류인지 확인
    public boolean isSubCategory() {
        return this.categoryType == CategoryType.SUB;
    }

    // 전체 경로 이름 반환 (예: "개발 > Java > Spring Boot")
    public String getFullPath() {
        if (this.parent != null) {
            return this.parent.getFullPath() + " > " + this.name;
        }
        return this.name;
    }
}
