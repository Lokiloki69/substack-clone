package com.substack.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestTagMappingId implements Serializable {
    private Long interest;
    private Long tag;
}
