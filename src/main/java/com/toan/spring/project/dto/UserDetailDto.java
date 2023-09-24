package com.toan.spring.project.dto;

import java.util.Set;

import com.toan.spring.project.models.Role;
import com.toan.spring.project.models.User;

import lombok.Data;

@Data
public class UserDetailDto {
    private String username;
    private String email;
    private String name;
    private Set<Role> roles;

    public UserDetailDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.roles = user.getRoles();
    }
}
