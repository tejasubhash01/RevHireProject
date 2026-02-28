package com.revhire.entity;

import com.revhire.entity.JobSeekerProfile;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeFile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_profile_id", nullable = false, unique = true)
    private JobSeekerProfile jobSeekerProfile;

    private String fileName;
    private String fileType;
    private String filePath;
    private Long fileSize;
    private LocalDateTime uploadDate;
}