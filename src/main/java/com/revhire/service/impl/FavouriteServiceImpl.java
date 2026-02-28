package com.revhire.service.impl;

import com.revhire.dto.favourites.FavouriteJobResponse;
import com.revhire.dto.jobs.JobPostDto;
import com.revhire.entity.FavouriteJob;
import com.revhire.entity.JobPost;
import com.revhire.entity.User;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.FavouriteJobRepository;
import com.revhire.repository.JobPostRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    private final UserRepository userRepository;
    private final JobPostRepository jobPostRepository;
    private final FavouriteJobRepository favouriteRepository;

    private User getUser(UserDetails userDetails) {
        log.debug("Fetching user with email: {}", userDetails.getUsername());
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userDetails.getUsername());
                    return new NotFoundException("User not found");
                });
    }

    private JobPost getJobPost(Long jobId) {
        log.debug("Fetching job post with id: {}", jobId);
        return jobPostRepository.findById(jobId)
                .orElseThrow(() -> {
                    log.error("Job post not found with id: {}", jobId);
                    return new NotFoundException("Job post not found");
                });
    }

    private FavouriteJobResponse mapToDto(FavouriteJob fav) {
        FavouriteJobResponse dto = new FavouriteJobResponse();
        dto.setId(fav.getId());
        JobPostDto jobDto = new JobPostDto();
        jobDto.setId(fav.getJobPost().getId());
        jobDto.setTitle(fav.getJobPost().getTitle());
        jobDto.setCompanyName(fav.getJobPost().getEmployer().getCompanyName());
        jobDto.setLocation(fav.getJobPost().getLocation());
        jobDto.setJobType(fav.getJobPost().getJobType());
        dto.setJobPost(jobDto);
        return dto;
    }

    @Override
    @Transactional
    public void addFavourite(UserDetails currentUser, Long jobId) {

        log.info("User {} attempting to add job {} to favourites",
                currentUser.getUsername(), jobId);

        User user = getUser(currentUser);
        JobPost jobPost = getJobPost(jobId);

        if (favouriteRepository.existsByUserIdAndJobPostId(user.getId(), jobId)) {
            log.warn("User {} tried to add job {} which is already in favourites",
                    user.getEmail(), jobId);
            throw new BadRequestException("Job already in favourites");
        }

        FavouriteJob fav = FavouriteJob.builder()
                .user(user)
                .jobPost(jobPost)
                .build();

        favouriteRepository.save(fav);

        log.info("Job {} successfully added to favourites for user {}",
                jobId, user.getEmail());
    }

    @Override
    public List<FavouriteJobResponse> getMyFavourites(UserDetails currentUser) {

        log.info("Fetching favourites for user {}", currentUser.getUsername());

        User user = getUser(currentUser);

        List<FavouriteJobResponse> favourites = favouriteRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        log.info("User {} has {} favourite jobs",
                user.getEmail(), favourites.size());

        return favourites;
    }

    @Override
    @Transactional
    public void removeFavourite(UserDetails currentUser, Long jobId) {

        log.info("User {} attempting to remove job {} from favourites",
                currentUser.getUsername(), jobId);

        User user = getUser(currentUser);

        FavouriteJob fav = favouriteRepository
                .findByUserIdAndJobPostId(user.getId(), jobId)
                .orElseThrow(() -> {
                    log.warn("Favourite not found for user {} and job {}",
                            user.getEmail(), jobId);
                    return new NotFoundException("Favourite not found");
                });

        favouriteRepository.delete(fav);

        log.info("Job {} removed from favourites for user {}",
                jobId, user.getEmail());
    }
}