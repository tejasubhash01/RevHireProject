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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {
    private final UserRepository userRepository;
    private final JobPostRepository jobPostRepository;
    private final FavouriteJobRepository favouriteRepository;

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private JobPost getJobPost(Long jobId) {
        return jobPostRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job post not found"));
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
        User user = getUser(currentUser);
        JobPost jobPost = getJobPost(jobId);
        if (favouriteRepository.existsByUserIdAndJobPostId(user.getId(), jobId))
            throw new BadRequestException("Job already in favourites");
        FavouriteJob fav = FavouriteJob.builder().user(user).jobPost(jobPost).build();
        favouriteRepository.save(fav);
    }

    @Override
    public List<FavouriteJobResponse> getMyFavourites(UserDetails currentUser) {
        User user = getUser(currentUser);
        return favouriteRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFavourite(UserDetails currentUser, Long jobId) {
        User user = getUser(currentUser);
        FavouriteJob fav = favouriteRepository.findByUserIdAndJobPostId(user.getId(), jobId)
                .orElseThrow(() -> new NotFoundException("Favourite not found"));
        favouriteRepository.delete(fav);
    }
}