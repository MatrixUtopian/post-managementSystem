package com.testExample.demo.repositories;

import com.testExample.demo.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserIdOrderByCreatedAtTimestampDesc(Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Post p ORDER BY p.createdAtTimestamp DESC")
    Page<Post> findAllOrderByCreatedAtTimestampDesc(Pageable pageable);
}

