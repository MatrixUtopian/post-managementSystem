package com.testExample.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "content")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description; // optional
    
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Media> mediaFiles = new ArrayList<>(); // optional
    
    @OneToOne(mappedBy = "content")
    private Post post;
}

