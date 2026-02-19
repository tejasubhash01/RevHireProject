package com.revhire.repository;

import com.revhire.entity.FavouriteJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavouriteJobRepository extends JpaRepository<FavouriteJob, Long> {
    List<FavouriteJob> findByUserId(Long userId);
    Optional<FavouriteJob> findByUserIdAndJobPostId(Long userId, Long jobPostId);
    boolean existsByUserIdAndJobPostId(Long userId, Long jobPostId);
}