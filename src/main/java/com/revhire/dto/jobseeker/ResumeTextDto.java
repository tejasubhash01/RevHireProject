package com.revhire.dto.jobseeker;

import lombok.Data;

@Data
public class ResumeTextDto {
    private Long id;
    private String objective;
    private String education;
    private String experience;
    private String skills;
    private String projects;
    private String certifications;
}