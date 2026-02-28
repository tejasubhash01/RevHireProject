package com.revhire.entity;

import com.revhire.entity.enums.JobType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "job_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPost extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private EmployerProfile employer;

    private String title;
    @Column(length = 5000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_post_id"))
    @Column(name = "skill")
    private List<String> requiredSkills;

    private String experienceRequired;
    private String educationRequired;
    private String location;
    private Double salaryMin;
    private Double salaryMax;
    @Enumerated(EnumType.STRING)
    private JobType jobType;
    private LocalDate applicationDeadline;
    private Integer numberOfOpenings;
    private Boolean isActive = true;
    private Boolean isFilled = false;


     @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
     private List<JobApplication> applications;
}