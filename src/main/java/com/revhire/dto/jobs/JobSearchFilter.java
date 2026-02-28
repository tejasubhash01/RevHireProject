package com.revhire.dto.jobs;

import com.revhire.entity.enums.JobType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class JobSearchFilter {
    private String title;
    private String location;
    private String company;
    private JobType jobType;
    private Integer minExp;
    private Integer maxExp;
    private Double salaryMin;
    private Double salaryMax;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate datePosted;
}