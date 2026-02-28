package com.revhire.dto.user;

import com.revhire.entity.enums.Role;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String location;
    private Role role;
}