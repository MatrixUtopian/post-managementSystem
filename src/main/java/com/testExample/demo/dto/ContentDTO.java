package com.testExample.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {
    private Long contentId;
    private String title;
    private String description; // optional
    private List<MediaDTO> mediaFiles = new ArrayList<>(); // optional
}

