package com.revhire.dto.jobseeker;

import com.revhire.entity.enums.EmploymentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateJobSeekerProfileRequest {
    @NotBlank private String headline;
    private String summary;
    private EmploymentStatus employmentStatus;
}