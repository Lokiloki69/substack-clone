package com.substack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicationDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private String ownerName;
    private Long subscriberCount;
    private Boolean isPaid;
}
