package com.revhire.dto.employer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEmployerProfileRequest {
    @NotBlank private String companyName;
    private String industry;
    private String companySize;
    private String companyDescription;
    private String website;
    private String location;
}