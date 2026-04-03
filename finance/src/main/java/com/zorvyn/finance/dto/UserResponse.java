package com.zorvyn.finance.dto;

import com.zorvyn.finance.model.Role;
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