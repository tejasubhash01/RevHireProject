package com.revhire.controller.admin;

import com.revhire.dto.ApiResponse;
import com.revhire.entity.enums.*;
import com.revhire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("Admin pong", "OK");
    }

    @GetMapping("/enums")
    public ApiResponse<Map<String, Object>> getEnums() {
        Map<String, Object> enums = new HashMap<>();
        enums.put("roles", Role.values());
        enums.put("jobTypes", JobType.values());
        enums.put("applicationStatus", ApplicationStatus.values());
        enums.put("employmentStatus", EmploymentStatus.values());
        enums.put("notificationTypes", NotificationType.values());
        return ApiResponse.success("Enums retrieved", enums);
    }

    @GetMapping("/db-check")
    public ApiResponse<Map<String, Object>> dbCheck() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userRepository.count());
        return ApiResponse.success("Database check", stats);
    }
}