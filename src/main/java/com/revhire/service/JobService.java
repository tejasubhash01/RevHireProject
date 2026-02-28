package com.revhire.service;

import com.revhire.dto.jobs.CreateJobPostRequest;
import com.revhire.dto.jobs.JobPostDto;
import com.revhire.dto.jobs.JobSearchFilter;
import com.revhire.dto.jobs.UpdateJobPostRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface JobService {
    JobPostDto createJobPost(UserDetails currentUser, CreateJobPostRequest request);
    JobPostDto getJobPostById(Long id);
    Page<JobPostDto> searchJobs(JobSearchFilter filter, Pageable pageable);
    JobPostDto updateJobPost(UserDetails currentUser, Long jobId, UpdateJobPostRequest request);
    void deleteJobPost(UserDetails currentUser, Long jobId);
    void closeJobPost(UserDetails currentUser, Long jobId);
    void reopenJobPost(UserDetails currentUser, Long jobId);
    void markJobFilled(UserDetails currentUser, Long jobId);
    Page<JobPostDto> getMyJobPosts(UserDetails currentUser, Pageable pageable);
}