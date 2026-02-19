package com.revhire.dto.applications;

import com.revhire.entity.enums.ApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private Long jobPostId;
    private String jobTitle;
    private String companyName;
    private Long jobSeekerId;
    private String jobSeekerName;
    private ApplicationStatus status;
    private String coverLetter;
    private LocalDateTime appliedDate;
}