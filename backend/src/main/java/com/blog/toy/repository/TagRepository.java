package com.blog.toy.repository;

import com.blog.toy.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    List<Tag> findByActiveOrderByNameAsc(boolean active);
    
    boolean existsByName(String name);
    
    @Query("SELECT t FROM Tag t WHERE t.active = true ORDER BY t.name ASC")
    List<Tag> findAllActiveOrdered();
    
    @Query("SELECT t FROM Tag t WHERE t.name IN :names AND t.active = true")
    List<Tag> findByNamesIn(List<String> names);
}
