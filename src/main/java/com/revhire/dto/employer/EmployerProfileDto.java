package com.revhire.dto.employer;

import lombok.Data;

@Data
public class EmployerProfileDto {
    private Long id;
    private Long userId;
    private String companyName;
    private String industry;
    private String companySize;
    private String companyDescription;
    private String website;
    private String location;
}