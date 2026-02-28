package com.revhire.dto.employer;

import lombok.Data;

@Data
public class EmployerDashboardDto {
    private Long totalJobs;
    private Long activeJobs;
    private Long totalApplications;
    private Long pendingReviews;
}