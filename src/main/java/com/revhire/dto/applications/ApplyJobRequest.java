package com.revhire.dto.applications;

import lombok.Data;

@Data
public class ApplyJobRequest {
    private Long jobPostId;
    private String coverLetter;
}