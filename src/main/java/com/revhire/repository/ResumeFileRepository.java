package com.revhire.repository;

import com.revhire.entity.ResumeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeFileRepository extends JpaRepository<ResumeFile, Long> {
    Optional<ResumeFile> findByJobSeekerProfileId(Long profileId);
}