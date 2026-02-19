package com.revhire.controller.jobseeker;

import com.revhire.dto.ApiResponse;
import com.revhire.dto.jobseeker.*;
import com.revhire.service.JobSeekerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/jobseekers")
@RequiredArgsConstructor
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;

    @PostMapping("/profile")
    public ApiResponse<JobSeekerProfileDto> createProfile(@AuthenticationPrincipal UserDetails currentUser,
                                                          @Valid @RequestBody CreateJobSeekerProfileRequest request) {
        JobSeekerProfileDto profile = jobSeekerService.createProfile(currentUser, request);
        return ApiResponse.success("Profile created successfully", profile);
    }

    @GetMapping("/profile/me")
    public ApiResponse<JobSeekerProfileDto> getMyProfile(@AuthenticationPrincipal UserDetails currentUser) {
        JobSeekerProfileDto profile = jobSeekerService.getMyProfile(currentUser);
        return ApiResponse.success("Profile retrieved", profile);
    }

    @PutMapping("/profile")
    public ApiResponse<JobSeekerProfileDto> updateProfile(@AuthenticationPrincipal UserDetails currentUser,
                                                          @Valid @RequestBody CreateJobSeekerProfileRequest request) {
        JobSeekerProfileDto profile = jobSeekerService.updateProfile(currentUser, request);
        return ApiResponse.success("Profile updated", profile);
    }

    @PostMapping("/resume/text")
    public ApiResponse<ResumeTextDto> createOrUpdateResumeText(@AuthenticationPrincipal UserDetails currentUser,
                                                               @Valid @RequestBody UpdateResumeTextRequest request) {
        ResumeTextDto resumeText = jobSeekerService.createOrUpdateResumeText(currentUser, request);
        return ApiResponse.success("Resume text saved", resumeText);
    }

    @GetMapping("/resume/text/me")
    public ApiResponse<ResumeTextDto> getMyResumeText(@AuthenticationPrincipal UserDetails currentUser) {
        ResumeTextDto resumeText = jobSeekerService.getMyResumeText(currentUser);
        return ApiResponse.success("Resume text retrieved", resumeText);
    }

    @PostMapping(value = "/resume/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ResumeFileDto> uploadResumeFile(@AuthenticationPrincipal UserDetails currentUser,
                                                       @RequestParam("file") MultipartFile file) throws IOException {
        ResumeFileDto resumeFile = jobSeekerService.uploadResumeFile(currentUser, file);
        return ApiResponse.success("Resume file uploaded", resumeFile);
    }

    @GetMapping("/resume/file/me")
    public ApiResponse<ResumeFileDto> getMyResumeFile(@AuthenticationPrincipal UserDetails currentUser) {
        ResumeFileDto resumeFile = jobSeekerService.getMyResumeFile(currentUser);
        return ApiResponse.success("Resume file info retrieved", resumeFile);
    }
}