package com.substack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    private String title;
    private String subTitle;
    private String content;
    private Boolean isPublished;
    private String authorName;
    private String createdAt;
    private Long likeCount;
    private Long commentCount;
    private Boolean liked;
}
