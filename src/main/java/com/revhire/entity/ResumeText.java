package com.revhire.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resume_texts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeText extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_profile_id", nullable = false, unique = true)
    private JobSeekerProfile jobSeekerProfile;

    @Column(columnDefinition = "TEXT")
    private String objective;
    @Column(columnDefinition = "TEXT")
    private String education;
    @Column(columnDefinition = "TEXT")
    private String experience;
    @Column(columnDefinition = "TEXT")
    private String skills;
    @Column(columnDefinition = "TEXT")
    private String projects;
    @Column(columnDefinition = "TEXT")
    private String certifications;
}