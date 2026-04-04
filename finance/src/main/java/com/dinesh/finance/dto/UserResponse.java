package com.dinesh.finance.dto;

import com.dinesh.finance.model.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean active;
}