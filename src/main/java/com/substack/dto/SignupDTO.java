package com.substack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupDTO {
    private String name;
    private String username;
    private String email;
    private String password;
    private Set<Long> interestIds = new HashSet<>();
}
