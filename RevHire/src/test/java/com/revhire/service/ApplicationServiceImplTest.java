package com.revhire.service;

import com.revhire.dto.applications.*;
import com.revhire.entity.*;
import com.revhire.entity.enums.ApplicationStatus;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.UnauthorizedException;
import com.revhire.repository.*;
import com.revhire.service.NotificationService;
import com.revhire.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private JobPostRepository jobPostRepository;
    @Mock private JobApplicationRepository applicationRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private UserDetails jobSeekerUserDetails;
    private UserDetails employerUserDetails;

    private User jobSeekerUser;
    private User employerUser;

    private JobSeekerProfile jobSeekerProfile;
    private EmployerProfile employerProfile;

    private JobPost jobPost;
    private JobApplication application;

    @BeforeEach
    void setUp() {

        jobSeekerUserDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("seeker@test.com")
                        .password("pass")
                        .roles("JOB_SEEKER")
                        .build();

        employerUserDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("employer@test.com")
                        .password("pass")
                        .roles("EMPLOYER")
                        .build();

        jobSeekerUser = new User();
        jobSeekerUser.setId(1L);
        jobSeekerUser.setEmail("seeker@test.com");
        jobSeekerUser.setName("Seeker");

        employerUser = new User();
        employerUser.setId(2L);
        employerUser.setEmail("employer@test.com");
        employerUser.setName("Employer");

        jobSeekerProfile = new JobSeekerProfile();
        jobSeekerProfile.setId(10L);
        jobSeekerProfile.setUser(jobSeekerUser);
        jobSeekerUser.setJobSeekerProfile(jobSeekerProfile);

        employerProfile = new EmployerProfile();
        employerProfile.setId(20L);
        employerProfile.setUser(employerUser);
        employerUser.setEmployerProfile(employerProfile);

        jobPost = new JobPost();
        jobPost.setId(100L);
        jobPost.setTitle("Java Developer");
        jobPost.setEmployer(employerProfile);
        jobPost.setIsActive(true);
        jobPost.setIsFilled(false);
        jobPost.setApplicationDeadline(LocalDate.now().plusDays(5));

        application = new JobApplication();
        application.setId(1000L);
        application.setJobPost(jobPost);
        application.setJobSeeker(jobSeekerProfile);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedDate(LocalDateTime.now());
    }

    @Test
    void applyForJob_success() {

        when(userRepository.findByEmail("seeker@test.com"))
                .thenReturn(Optional.of(jobSeekerUser));

        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));

        when(applicationRepository.existsByJobPostIdAndJobSeekerId(100L, 10L))
                .thenReturn(false);

        when(applicationRepository.save(any(JobApplication.class)))
                .thenReturn(application);

        ApplyJobRequest request = new ApplyJobRequest();
        request.setJobPostId(100L);
        request.setCoverLetter("Interested");

        ApplicationResponse response =
                applicationService.applyForJob(jobSeekerUserDetails, request);

        assertNotNull(response);
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());

        verify(notificationService, times(1))
                .createNotification(any(), anyString(), anyString(), anyString());
    }

    @Test
    void applyForJob_alreadyApplied_shouldThrow() {

        when(userRepository.findByEmail("seeker@test.com"))
                .thenReturn(Optional.of(jobSeekerUser));

        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));

        when(applicationRepository.existsByJobPostIdAndJobSeekerId(100L, 10L))
                .thenReturn(true);

        ApplyJobRequest request = new ApplyJobRequest();
        request.setJobPostId(100L);

        assertThrows(BadRequestException.class,
                () -> applicationService.applyForJob(jobSeekerUserDetails, request));
    }

    @Test
    void getMyApplications_success() {

        when(userRepository.findByEmail("seeker@test.com"))
                .thenReturn(Optional.of(jobSeekerUser));

        when(applicationRepository.findByJobSeekerId(10L))
                .thenReturn(List.of(application));

        List<ApplicationResponse> responses =
                applicationService.getMyApplications(jobSeekerUserDetails);

        assertEquals(1, responses.size());
        assertEquals(1000L, responses.get(0).getId());
    }

    @Test
    void withdrawApplication_success() {

        when(userRepository.findByEmail("seeker@test.com"))
                .thenReturn(Optional.of(jobSeekerUser));

        when(applicationRepository.findById(1000L))
                .thenReturn(Optional.of(application));

        when(applicationRepository.save(any(JobApplication.class)))
                .thenReturn(application);

        WithdrawApplicationRequest request = new WithdrawApplicationRequest();
        request.setReason("Another offer");

        applicationService.withdrawApplication(
                jobSeekerUserDetails, 1000L, request);

        assertEquals(ApplicationStatus.WITHDRAWN, application.getStatus());

        verify(notificationService, times(1))
                .createNotification(any(), anyString(), anyString(), anyString());
    }

    @Test
    void withdrawApplication_notOwner_shouldThrow() {

        when(userRepository.findByEmail("seeker@test.com"))
                .thenReturn(Optional.of(jobSeekerUser));

        JobSeekerProfile differentProfile = new JobSeekerProfile();
        differentProfile.setId(999L);
        application.setJobSeeker(differentProfile);

        when(applicationRepository.findById(1000L))
                .thenReturn(Optional.of(application));

        assertThrows(UnauthorizedException.class,
                () -> applicationService.withdrawApplication(
                        jobSeekerUserDetails, 1000L, null));
    }

    @Test
    void updateApplicationStatus_success() {

        when(userRepository.findByEmail("employer@test.com"))
                .thenReturn(Optional.of(employerUser));

        when(applicationRepository.findById(1000L))
                .thenReturn(Optional.of(application));

        when(applicationRepository.save(any(JobApplication.class)))
                .thenReturn(application);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest();
        request.setStatus(ApplicationStatus.SHORTLISTED);
        request.setEmployerNotes("Strong candidate");

        ApplicationResponse response =
                applicationService.updateApplicationStatus(
                        employerUserDetails, 1000L, request);

        assertEquals(ApplicationStatus.SHORTLISTED, response.getStatus());

        verify(notificationService, times(1))
                .createNotification(any(), anyString(), anyString(), anyString());
    }

    @Test
    void addNote_success() {

        when(userRepository.findByEmail("employer@test.com"))
                .thenReturn(Optional.of(employerUser));

        when(applicationRepository.findById(1000L))
                .thenReturn(Optional.of(application));

        when(applicationRepository.save(any(JobApplication.class)))
                .thenReturn(application);

        NoteRequest request = new NoteRequest();
        request.setNote("Interview Monday");

        ApplicationResponse response =
                applicationService.addNote(
                        employerUserDetails, 1000L, request);

        assertEquals("Interview Monday", application.getEmployerNotes());
        verify(applicationRepository, times(1)).save(application);
    }
}