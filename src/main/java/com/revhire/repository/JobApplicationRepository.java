package com.revhire.repository;

import com.revhire.entity.JobApplication;
import com.revhire.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobSeekerId(Long jobSeekerId);
    List<JobApplication> findByJobPostId(Long jobPostId);
    boolean existsByJobPostIdAndJobSeekerId(Long jobPostId, Long jobSeekerId);
}