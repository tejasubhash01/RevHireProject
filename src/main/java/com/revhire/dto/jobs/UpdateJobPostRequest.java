package com.revhire.dto.jobs;

import com.revhire.entity.enums.JobType;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateJobPostRequest {
    private String title;
    private String description;
    private List<String> requiredSkills;
    private String experienceRequired;
    private String educationRequired;
    private String location;
    private Double salaryMin;
    private Double salaryMax;
    private JobType jobType;
    private LocalDate applicationDeadline;
    private Integer numberOfOpenings;
    private Boolean isActive;
    private Boolean isFilled;
}