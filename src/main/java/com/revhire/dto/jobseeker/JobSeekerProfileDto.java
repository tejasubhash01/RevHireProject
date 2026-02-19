package com.revhire.dto.jobseeker;

import com.revhire.entity.enums.EmploymentStatus;
import lombok.Data;

@Data
public class JobSeekerProfileDto {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String location;
    private EmploymentStatus employmentStatus;
    private String headline;
    private String summary;
    private ResumeTextDto resumeText;
    private ResumeFileDto resumeFile;
}