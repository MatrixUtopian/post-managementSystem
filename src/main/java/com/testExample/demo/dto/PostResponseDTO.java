package com.testExample.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long postId;
    private Long userId;
    private LocalDateTime createdAtTimestamp;
    private LocalDateTime updatedAtTimestamp;
    private ContentDTO content;
}

