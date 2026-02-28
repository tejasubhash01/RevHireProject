package com.revhire.repository;

import com.revhire.entity.ResumeText;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeTextRepository extends JpaRepository<ResumeText, Long> {
    Optional<ResumeText> findByJobSeekerProfileId(Long profileId);
}