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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {
    private final UserRepository userRepository;
    private final EmployerProfileRepository profileRepository;
    private final JobPostRepository jobPostRepository;
    private final JobApplicationRepository applicationRepository;

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private EmployerProfile getProfile(User user) {
        if (user.getEmployerProfile() == null)
            throw new NotFoundException("Employer profile not found");
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
        User user = getUser(currentUser);
        if (user.getEmployerProfile() != null)
            throw new BadRequestException("Profile already exists");
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
        return mapToDto(profile);
    }

    @Override
    public EmployerProfileDto getMyProfile(UserDetails currentUser) {
        User user = getUser(currentUser);
        EmployerProfile profile = getProfile(user);
        return mapToDto(profile);
    }

    @Override
    @Transactional
    public EmployerProfileDto updateProfile(UserDetails currentUser, CreateEmployerProfileRequest request) {
        User user = getUser(currentUser);
        EmployerProfile profile = getProfile(user);
        if (request.getCompanyName() != null) profile.setCompanyName(request.getCompanyName());
        if (request.getIndustry() != null) profile.setIndustry(request.getIndustry());
        if (request.getCompanySize() != null) profile.setCompanySize(request.getCompanySize());
        if (request.getCompanyDescription() != null) profile.setCompanyDescription(request.getCompanyDescription());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        profile = profileRepository.save(profile);
        return mapToDto(profile);
    }

    @Override
    public EmployerDashboardDto getDashboardStats(UserDetails currentUser) {
        User user = getUser(currentUser);
        EmployerProfile profile = getProfile(user);
        List<JobPost> jobPosts = jobPostRepository.findByEmployerId(profile.getId());
        Long totalJobs = (long) jobPosts.size();
        Long activeJobs = jobPosts.stream().filter(JobPost::getIsActive).count();
        Long totalApplications = jobPosts.stream().mapToLong(jp -> jp.getApplications().size()).sum();
        Long pendingReviews = jobPosts.stream()
                .flatMap(jp -> jp.getApplications().stream())
                .filter(app -> app.getStatus() == ApplicationStatus.APPLIED || app.getStatus() == ApplicationStatus.UNDER_REVIEW)
                .count();
        EmployerDashboardDto dto = new EmployerDashboardDto();
        dto.setTotalJobs(totalJobs);
        dto.setActiveJobs(activeJobs);
        dto.setTotalApplications(totalApplications);
        dto.setPendingReviews(pendingReviews);
        return dto;
    }
}