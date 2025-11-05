package com.substack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {
    private Long id;
    private String authorName;
    private String authorUsername;
    private String type;
    private Double amount;
    private String startDate;
    private String endDate;
    private Boolean active;
}
