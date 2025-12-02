package com.testExample.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAtTimestamp;
    
    @Column
    private LocalDateTime updatedAtTimestamp; // null initially
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAtTimestamp = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAtTimestamp = LocalDateTime.now();
    }
}

