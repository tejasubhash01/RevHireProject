package com.revhire.controller.admin;

import com.revhire.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("Admin pong", "OK");
    }
}