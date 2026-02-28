package com.revhire.controller.applications;

import com.revhire.dto.ApiResponse;
import com.revhire.dto.applications.*;
import com.revhire.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply")
    public ApiResponse<ApplicationResponse> applyForJob(@AuthenticationPrincipal UserDetails currentUser,
                                                        @Valid @RequestBody ApplyJobRequest request) {
        ApplicationResponse response = applicationService.applyForJob(currentUser, request);
        return ApiResponse.success("Application submitted", response);
    }

    @GetMapping("/my")
    public ApiResponse<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal UserDetails currentUser) {
        List<ApplicationResponse> applications = applicationService.getMyApplications(currentUser);
        return ApiResponse.success("Applications retrieved", applications);
    }

    @PatchMapping("/{id}/withdraw")
    public ApiResponse<Void> withdrawApplication(@AuthenticationPrincipal UserDetails currentUser,
                                                 @PathVariable Long id,
                                                 @RequestBody(required = false) WithdrawApplicationRequest request) {
        applicationService.withdrawApplication(currentUser, id, request);
        return ApiResponse.success("Application withdrawn", null);
    }

    @GetMapping("/job/{jobId}")
    public ApiResponse<List<ApplicationResponse>> getApplicationsForJob(@AuthenticationPrincipal UserDetails currentUser,
                                                                        @PathVariable Long jobId) {
        List<ApplicationResponse> applications = applicationService.getApplicationsForJob(currentUser, jobId);
        return ApiResponse.success("Applications retrieved", applications);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ApplicationResponse> updateApplicationStatus(@AuthenticationPrincipal UserDetails currentUser,
                                                                    @PathVariable Long id,
                                                                    @Valid @RequestBody UpdateApplicationStatusRequest request) {
        ApplicationResponse response = applicationService.updateApplicationStatus(currentUser, id, request);
        return ApiResponse.success("Application status updated", response);
    }

    @PatchMapping("/{id}/note")
    public ApiResponse<ApplicationResponse> addNote(@AuthenticationPrincipal UserDetails currentUser,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody NoteRequest request) {
        ApplicationResponse response = applicationService.addNote(currentUser, id, request);
        return ApiResponse.success("Note added", response);
    }
}