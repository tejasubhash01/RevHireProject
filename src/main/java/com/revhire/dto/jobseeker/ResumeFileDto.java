package com.revhire.dto.jobseeker;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResumeFileDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;
}