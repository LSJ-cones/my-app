package com.blog.toy.repository;

import com.blog.toy.domain.File;
import com.blog.toy.domain.Post;
import com.blog.toy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByPost(Post post);
    
    List<File> findByUser(User user);
    
    Optional<File> findByStoredFileName(String storedFileName);
    
    void deleteByPost(Post post);
    
    void deleteByUser(User user);
}
