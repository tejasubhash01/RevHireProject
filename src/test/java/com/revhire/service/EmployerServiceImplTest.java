package com.revhire.service;

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
import com.revhire.service.impl.EmployerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployerProfileRepository profileRepository;

    @Mock
    private JobPostRepository jobPostRepository;

    @Mock
    private JobApplicationRepository applicationRepository;

    @InjectMocks
    private EmployerServiceImpl employerService;

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("employer@test.com")
                .password("pass")
                .roles("EMPLOYER")
                .build();

        user = User.builder()
                .email("employer@test.com")
                .name("Employer")
                .build();
        user.setId(1L);
    }

    @Test
    void createProfile_success() {
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));

        CreateEmployerProfileRequest request = new CreateEmployerProfileRequest();
        request.setCompanyName("TechCorp");
        request.setIndustry("IT");

        EmployerProfile savedProfile = EmployerProfile.builder()
                .user(user)
                .companyName("TechCorp")
                .industry("IT")
                .build();
        savedProfile.setId(10L);

        when(profileRepository.save(any(EmployerProfile.class)))
                .thenReturn(savedProfile);

        EmployerProfileDto result = employerService.createProfile(userDetails, request);

        assertEquals("TechCorp", result.getCompanyName());
        assertEquals("IT", result.getIndustry());
    }

    @Test
    void createProfile_alreadyExists_shouldThrow() {
        EmployerProfile profile = EmployerProfile.builder().build();
        user.setEmployerProfile(profile);

        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));

        CreateEmployerProfileRequest request = new CreateEmployerProfileRequest();

        assertThrows(BadRequestException.class,
                () -> employerService.createProfile(userDetails, request));
    }

    @Test
    void getMyProfile_success() {
        EmployerProfile profile = EmployerProfile.builder()
                .user(user)
                .companyName("TechCorp")
                .build();
        profile.setId(10L);
        user.setEmployerProfile(profile);

        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));

        EmployerProfileDto dto = employerService.getMyProfile(userDetails);

        assertEquals("TechCorp", dto.getCompanyName());
    }

    @Test
    void updateProfile_success() {
        EmployerProfile profile = EmployerProfile.builder()
                .user(user)
                .companyName("OldName")
                .build();
        profile.setId(10L);
        user.setEmployerProfile(profile);

        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));

        when(profileRepository.save(any(EmployerProfile.class)))
                .thenReturn(profile);

        CreateEmployerProfileRequest request = new CreateEmployerProfileRequest();
        request.setCompanyName("NewName");

        EmployerProfileDto dto = employerService.updateProfile(userDetails, request);

        assertEquals("NewName", dto.getCompanyName());
    }

    @Test
    void getDashboardStats_success() {
        EmployerProfile profile = EmployerProfile.builder()
                .user(user)
                .companyName("TechCorp")
                .build();
        profile.setId(10L);
        user.setEmployerProfile(profile);

        JobApplication app1 = JobApplication.builder()
                .status(ApplicationStatus.APPLIED)
                .build();

        JobApplication app2 = JobApplication.builder()
                .status(ApplicationStatus.UNDER_REVIEW)
                .build();

        JobPost job1 = JobPost.builder()
                .isActive(true)
                .build();
        job1.setApplications(List.of(app1, app2));

        JobPost job2 = JobPost.builder()
                .isActive(false)
                .build();
        job2.setApplications(List.of());

        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));

        when(jobPostRepository.findByEmployerId(profile.getId()))
                .thenReturn(List.of(job1, job2));

        EmployerDashboardDto dto = employerService.getDashboardStats(userDetails);

        assertEquals(2L, dto.getTotalJobs());
        assertEquals(1L, dto.getActiveJobs());
        assertEquals(2L, dto.getTotalApplications());
        assertEquals(2L, dto.getPendingReviews());
    }

    @Test
    void getUser_notFound_shouldThrow() {
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> employerService.getMyProfile(userDetails));
    }
}