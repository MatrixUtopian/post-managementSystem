package com.testExample.demo.services;

import com.testExample.demo.dto.*;
import com.testExample.demo.entities.*;
import com.testExample.demo.repositories.PostRepository;
import com.testExample.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Convert Entity to DTO
    private PostResponseDTO convertToDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setCreatedAtTimestamp(post.getCreatedAtTimestamp());
        dto.setUpdatedAtTimestamp(post.getUpdatedAtTimestamp());
        
        if (post.getContent() != null) {
            dto.setContent(convertContentToDTO(post.getContent()));
        }
        
        return dto;
    }
    
    private ContentDTO convertContentToDTO(Content content) {
        ContentDTO dto = new ContentDTO();
        dto.setContentId(content.getContentId());
        dto.setTitle(content.getTitle());
        dto.setDescription(content.getDescription());
        
        if (content.getMediaFiles() != null && !content.getMediaFiles().isEmpty()) {
            dto.setMediaFiles(content.getMediaFiles().stream()
                    .map(this::convertMediaToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private MediaDTO convertMediaToDTO(Media media) {
        MediaDTO dto = new MediaDTO();
        dto.setMediaId(media.getMediaId());
        dto.setMediaUrl(media.getMediaUrl());
        dto.setMediaType(media.getMediaType());
        return dto;
    }
    
    // Convert DTO to Entity
    private Post convertToEntity(PostRequestDTO dto) {
        Post post = new Post();
        post.setUserId(dto.getUserId());
        
        Content content = new Content();
        content.setTitle(dto.getContent().getTitle());
        content.setDescription(dto.getContent().getDescription());
        
        if (dto.getContent().getMediaFiles() != null && !dto.getContent().getMediaFiles().isEmpty()) {
            List<Media> mediaList = dto.getContent().getMediaFiles().stream()
                    .map(mediaDTO -> {
                        Media media = new Media();
                        media.setMediaUrl(mediaDTO.getMediaUrl());
                        media.setMediaType(mediaDTO.getMediaType());
                        media.setContent(content);
                        return media;
                    })
                    .collect(Collectors.toList());
            content.setMediaFiles(mediaList);
        }
        
        post.setContent(content);
        content.setPost(post);
        
        return post;
    }
    
    // Get a post by ID
    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        return convertToDTO(post);
    }
    
    // Get all posts (timeline - newest first) with pagination
    public PaginatedPostResponseDTO getAllPosts(int page, int size) {
        // Ensure size doesn't exceed 10
        int pageSize = Math.min(size, 10);
        // Ensure page is at least 0
        int pageNumber = Math.max(page, 0);
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Post> postPage = postRepository.findAllOrderByCreatedAtTimestampDesc(pageable);
        
        List<PostResponseDTO> postDTOs = postPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return createPaginatedResponse(postDTOs, postPage);
    }
    
    // Get posts by user ID with pagination
    public PaginatedPostResponseDTO getPostsByUserId(Long userId, int page, int size) {
        // Ensure size doesn't exceed 10
        int pageSize = Math.min(size, 10);
        // Ensure page is at least 0
        int pageNumber = Math.max(page, 0);
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtTimestampDesc(userId, pageable);
        
        List<PostResponseDTO> postDTOs = postPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return createPaginatedResponse(postDTOs, postPage);
    }
    
    // Helper method to create paginated response
    private PaginatedPostResponseDTO createPaginatedResponse(List<PostResponseDTO> posts, Page<Post> postPage) {
        PaginatedPostResponseDTO response = new PaginatedPostResponseDTO();
        response.setPosts(posts);
        response.setCurrentPage(postPage.getNumber());
        response.setPageSize(postPage.getSize());
        response.setTotalElements(postPage.getTotalElements());
        response.setTotalPages(postPage.getTotalPages());
        response.setHasNext(postPage.hasNext());
        response.setHasPrevious(postPage.hasPrevious());
        return response;
    }
    
    // Create a new post
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        // Verify user exists
        userRepository.findById(postRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + postRequestDTO.getUserId()));
        
        Post post = convertToEntity(postRequestDTO);
        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }
    
    // Update a post
    public PostResponseDTO updatePost(Long id, PostRequestDTO postRequestDTO) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
        // Update content
        Content content = existingPost.getContent();
        if (content == null) {
            content = new Content();
            existingPost.setContent(content);
            content.setPost(existingPost);
        }
        
        final Content finalContent = content; // Make effectively final for lambda
        
        finalContent.setTitle(postRequestDTO.getContent().getTitle());
        finalContent.setDescription(postRequestDTO.getContent().getDescription());
        
        // Update media files
        if (postRequestDTO.getContent().getMediaFiles() != null) {
            // Clear existing media
            finalContent.getMediaFiles().clear();
            
            // Add new media
            List<Media> mediaList = postRequestDTO.getContent().getMediaFiles().stream()
                    .map(mediaDTO -> {
                        Media media = new Media();
                        media.setMediaUrl(mediaDTO.getMediaUrl());
                        media.setMediaType(mediaDTO.getMediaType());
                        media.setContent(finalContent);
                        return media;
                    })
                    .collect(Collectors.toList());
            finalContent.getMediaFiles().addAll(mediaList);
        }
        
        Post updatedPost = postRepository.save(existingPost);
        return convertToDTO(updatedPost);
    }
    
    // Delete a post
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }
}

