package com.revhire.entity;

import com.revhire.entity.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_seeker_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    private String headline;
    @Column(length = 1000)
    private String summary;

    @OneToOne(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL)
    private ResumeText resumeText;

    @OneToOne(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL)
    private ResumeFile resumeFile;
}