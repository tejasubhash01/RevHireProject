package com.revhire.controller.jobs;

import com.revhire.dto.ApiResponse;
import com.revhire.dto.jobs.CreateJobPostRequest;
import com.revhire.dto.jobs.JobPostDto;
import com.revhire.dto.jobs.JobSearchFilter;
import com.revhire.dto.jobs.UpdateJobPostRequest;
import com.revhire.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ApiResponse<JobPostDto> createJobPost(@AuthenticationPrincipal UserDetails currentUser,
                                                 @Valid @RequestBody CreateJobPostRequest request) {
        JobPostDto jobPost = jobService.createJobPost(currentUser, request);
        return ApiResponse.success("Job post created", jobPost);
    }

    @GetMapping("/{id}")
    public ApiResponse<JobPostDto> getJobPostById(@PathVariable Long id) {
        JobPostDto jobPost = jobService.getJobPostById(id);
        return ApiResponse.success("Job post retrieved", jobPost);
    }

    @PutMapping("/{id}")
    public ApiResponse<JobPostDto> updateJobPost(@AuthenticationPrincipal UserDetails currentUser,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody UpdateJobPostRequest request) {
        JobPostDto jobPost = jobService.updateJobPost(currentUser, id, request);
        return ApiResponse.success("Job post updated", jobPost);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteJobPost(@AuthenticationPrincipal UserDetails currentUser,
                                           @PathVariable Long id) {
        jobService.deleteJobPost(currentUser, id);
        return ApiResponse.success("Job post deleted", null);
    }

    @PatchMapping("/{id}/close")
    public ApiResponse<Void> closeJobPost(@AuthenticationPrincipal UserDetails currentUser,
                                          @PathVariable Long id) {
        jobService.closeJobPost(currentUser, id);
        return ApiResponse.success("Job post closed", null);
    }

    @PatchMapping("/{id}/reopen")
    public ApiResponse<Void> reopenJobPost(@AuthenticationPrincipal UserDetails currentUser,
                                           @PathVariable Long id) {
        jobService.reopenJobPost(currentUser, id);
        return ApiResponse.success("Job post reopened", null);
    }

    @PatchMapping("/{id}/mark-filled")
    public ApiResponse<Void> markJobFilled(@AuthenticationPrincipal UserDetails currentUser,
                                           @PathVariable Long id) {
        jobService.markJobFilled(currentUser, id);
        return ApiResponse.success("Job marked as filled", null);
    }

    @GetMapping("/search")
    public ApiResponse<Page<JobPostDto>> searchJobs(JobSearchFilter filter,
                                                    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<JobPostDto> jobs = jobService.searchJobs(filter, pageable);
        return ApiResponse.success("Jobs retrieved", jobs);
    }

    @GetMapping("/my")
    public ApiResponse<Page<JobPostDto>> getMyJobs(@AuthenticationPrincipal UserDetails currentUser,
                                                   @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<JobPostDto> jobs = jobService.getMyJobPosts(currentUser, pageable);
        return ApiResponse.success("Your job posts retrieved", jobs);
    }
}