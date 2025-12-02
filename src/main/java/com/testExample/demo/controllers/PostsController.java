package com.testExample.demo.controllers;

import com.testExample.demo.dto.PaginatedPostResponseDTO;
import com.testExample.demo.dto.PostRequestDTO;
import com.testExample.demo.dto.PostResponseDTO;
import com.testExample.demo.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostsController {
    
    @Autowired
    private PostService postService;
    
    // GET /posts/{id} - Get a post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long id) {
        try {
            PostResponseDTO post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /posts - List all posts (timeline - newest first) with pagination
    @GetMapping
    public ResponseEntity<PaginatedPostResponseDTO> getAllPosts(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (userId != null) {
            // GET /posts?userId={userId}&page={page}&size={size} - Get posts of a user with pagination
            PaginatedPostResponseDTO response = postService.getPostsByUserId(userId, page, size);
            return ResponseEntity.ok(response);
        } else {
            // GET /posts?page={page}&size={size} - Get all posts (timeline) with pagination
            PaginatedPostResponseDTO response = postService.getAllPosts(page, size);
            return ResponseEntity.ok(response);
        }
    }
    
    // GET /posts/user/{userId} - Alternative endpoint to get posts of a user with pagination
    @GetMapping("/user/{userId}")
    public ResponseEntity<PaginatedPostResponseDTO> getPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedPostResponseDTO response = postService.getPostsByUserId(userId, page, size);
        return ResponseEntity.ok(response);
    }
    
    // POST /posts/create - Create a new post
    @PostMapping("/create")
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO postRequestDTO) {
        try {
            PostResponseDTO createdPost = postService.createPost(postRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DELETE /posts/{id} - Delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // PUT /posts/{id} - Update a post (full update)
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequestDTO postRequestDTO) {
        try {
            PostResponseDTO updatedPost = postService.updatePost(id, postRequestDTO);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // PATCH /posts/{id} - Update a post (partial update)
    @PatchMapping("/{id}")
    public ResponseEntity<PostResponseDTO> patchPost(
            @PathVariable Long id,
            @RequestBody PostRequestDTO postRequestDTO) {
        try {
            PostResponseDTO updatedPost = postService.updatePost(id, postRequestDTO);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

