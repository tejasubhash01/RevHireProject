package com.revhire.service.impl;

import com.revhire.dto.employer.CreateEmployerProfileRequest;
import com.revhire.dto.employer.EmployerDashboardDto;
import com.revhire.dto.employer.EmployerProfileDto;
import com.revhire.entity.EmployerProfile;
import com.revhire.entity.JobApplication;
import com.revhire.entity.JobPost;
import com.revhire.entity.User;
import com.revhire.entity.enums.ApplicationStatus;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.EmployerProfileRepository;
import com.revhire.repository.JobApplicationRepository;
import com.revhire.repository.JobPostRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.EmployerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final UserRepository userRepository;
    private final EmployerProfileRepository profileRepository;
    private final JobPostRepository jobPostRepository;
    private final JobApplicationRepository applicationRepository;

    private User getUser(UserDetails userDetails) {
        log.debug("Fetching user with email: {}", userDetails.getUsername());
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userDetails.getUsername());
                    return new NotFoundException("User not found");
                });
    }

    private EmployerProfile getProfile(User user) {
        if (user.getEmployerProfile() == null) {
            log.error("Employer profile not found for user: {}", user.getEmail());
            throw new NotFoundException("Employer profile not found");
        }
        return user.getEmployerProfile();
    }

    private EmployerProfileDto mapToDto(EmployerProfile profile) {
        EmployerProfileDto dto = new EmployerProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setCompanyName(profile.getCompanyName());
        dto.setIndustry(profile.getIndustry());
        dto.setCompanySize(profile.getCompanySize());
        dto.setCompanyDescription(profile.getCompanyDescription());
        dto.setWebsite(profile.getWebsite());
        dto.setLocation(profile.getLocation());
        return dto;
    }

    @Override
    @Transactional
    public EmployerProfileDto createProfile(UserDetails currentUser, CreateEmployerProfileRequest request) {

        log.info("User {} attempting to create employer profile", currentUser.getUsername());

        User user = getUser(currentUser);

        if (user.getEmployerProfile() != null) {
            log.warn("Profile creation attempt failed — profile already exists for user {}",
                    user.getEmail());
            throw new BadRequestException("Profile already exists");
        }

        EmployerProfile profile = EmployerProfile.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .industry(request.getIndustry())
                .companySize(request.getCompanySize())
                .companyDescription(request.getCompanyDescription())
                .website(request.getWebsite())
                .location(request.getLocation())
                .build();

        profile = profileRepository.save(profile);

        log.info("Employer profile created successfully with id {}", profile.getId());

        return mapToDto(profile);
    }

    @Override
    public EmployerProfileDto getMyProfile(UserDetails currentUser) {

        log.info("Fetching employer profile for user {}", currentUser.getUsername());

        User user = getUser(currentUser);
        EmployerProfile profile = getProfile(user);

        log.debug("Employer profile found with id {}", profile.getId());

        return mapToDto(profile);
    }

    @Override
    @Transactional
    public EmployerProfileDto updateProfile(UserDetails currentUser, CreateEmployerProfileRequest request) {

        log.info("User {} attempting to update employer profile",
                currentUser.getUsername());

        User user = getUser(currentUser);
        EmployerProfile profile = getProfile(user);

        if (request.getCompanyName() != null) profile.setCompanyName(request.getCompanyName());
        if (request.getIndustry() != null) profile.setIndustry(request.getIndustry());
        if (request.getCompanySize() != null) profile.setCompanySize(request.getCompanySize());
        if (request.getCompanyDescription() != null) profile.setCompanyDescription(request.getCompanyDescription());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());

        profile = profileRepository.save(profile);

        log.info("Employer profile updated successfully for user {}",
                user.getEmail());

        return mapToDto(profile);
    }

    @Override
    public EmployerDashboardDto getDashboardStats(UserDetails currentUser) {

        log.info("Generating dashboard statistics for employer {}",
                currentUser.getUsername());

        User user = getUser(currentUser);
        EmployerProfile profile = getProfile(user);

        List<JobPost> jobPosts = jobPostRepository.findByEmployerId(profile.getId());

        Long totalJobs = (long) jobPosts.size();
        Long activeJobs = jobPosts.stream().filter(JobPost::getIsActive).count();
        Long totalApplications = jobPosts.stream().mapToLong(jp -> jp.getApplications().size()).sum();
        Long pendingReviews = jobPosts.stream()
                .flatMap(jp -> jp.getApplications().stream())
                .filter(app -> app.getStatus() == ApplicationStatus.APPLIED
                        || app.getStatus() == ApplicationStatus.UNDER_REVIEW)
                .count();

        log.debug("Dashboard stats — totalJobs: {}, activeJobs: {}, totalApplications: {}, pendingReviews: {}",
                totalJobs, activeJobs, totalApplications, pendingReviews);

        EmployerDashboardDto dto = new EmployerDashboardDto();
        dto.setTotalJobs(totalJobs);
        dto.setActiveJobs(activeJobs);
        dto.setTotalApplications(totalApplications);
        dto.setPendingReviews(pendingReviews);

        log.info("Dashboard statistics generated successfully for employer {}",
                user.getEmail());

        return dto;
    }
}