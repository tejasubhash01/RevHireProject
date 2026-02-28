package com.revhire.dto.jobs;

import com.revhire.entity.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateJobPostRequest {
    @NotBlank private String title;
    @NotBlank private String description;
    private List<String> requiredSkills;
    private String experienceRequired;
    private String educationRequired;
    private String location;
    private Double salaryMin;
    private Double salaryMax;
    @NotNull private JobType jobType;
    private LocalDate applicationDeadline;
    private Integer numberOfOpenings;
}