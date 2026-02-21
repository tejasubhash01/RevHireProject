package com.revhire.service;

import com.revhire.dto.jobs.CreateJobPostRequest;
import com.revhire.dto.jobs.JobPostDto;
import com.revhire.dto.jobs.UpdateJobPostRequest;
import com.revhire.entity.EmployerProfile;
import com.revhire.entity.JobPost;
import com.revhire.entity.User;
import com.revhire.entity.enums.JobType;
import com.revhire.exception.NotFoundException;
import com.revhire.exception.UnauthorizedException;
import com.revhire.repository.EmployerProfileRepository;
import com.revhire.repository.JobPostRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.impl.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployerProfileRepository employerProfileRepository;

    @Mock
    private JobPostRepository jobPostRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private UserDetails userDetails;
    private User user;
    private EmployerProfile employer;
    private JobPost jobPost;

    @BeforeEach
    void setUp() {
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("employer@test.com")
                .password("pass")
                .roles("EMPLOYER")
                .build();

        user = User.builder().email("employer@test.com").build();
        user.setId(1L);

        employer = EmployerProfile.builder()
                .user(user)
                .companyName("TechCorp")
                .build();
        employer.setId(10L);

        user.setEmployerProfile(employer);

        jobPost = JobPost.builder()
                .employer(employer)
                .title("Java Developer")
                .description("Backend role")
                .jobType(JobType.FULL_TIME)
                .isActive(true)
                .isFilled(false)
                .applicationDeadline(LocalDate.now().plusDays(10))
                .build();
        jobPost.setId(100L);
    }

    @Test
    void createJobPost_success() {
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        when(jobPostRepository.save(any(JobPost.class)))
                .thenReturn(jobPost);

        CreateJobPostRequest request = new CreateJobPostRequest();
        request.setTitle("Java Developer");
        request.setDescription("Backend role");
        request.setJobType(JobType.FULL_TIME);

        JobPostDto dto = jobService.createJobPost(userDetails, request);

        assertEquals("Java Developer", dto.getTitle());
        assertEquals("TechCorp", dto.getCompanyName());
    }

    @Test
    void getJobPostById_success() {
        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));

        JobPostDto dto = jobService.getJobPostById(100L);

        assertEquals("Java Developer", dto.getTitle());
    }

    @Test
    void updateJobPost_success() {
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));
        when(jobPostRepository.save(any(JobPost.class)))
                .thenReturn(jobPost);

        UpdateJobPostRequest request = new UpdateJobPostRequest();
        request.setTitle("Senior Java Developer");

        JobPostDto dto = jobService.updateJobPost(userDetails, 100L, request);

        assertEquals("Senior Java Developer", dto.getTitle());
    }

    @Test
    void updateJobPost_unauthorized_shouldThrow() {
        EmployerProfile otherEmployer = EmployerProfile.builder().build();
        otherEmployer.setId(999L);
        jobPost.setEmployer(otherEmployer);

        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));

        UpdateJobPostRequest request = new UpdateJobPostRequest();

        assertThrows(UnauthorizedException.class,
                () -> jobService.updateJobPost(userDetails, 100L, request));
    }

    @Test
    void deleteJobPost_success() {
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));

        jobService.deleteJobPost(userDetails, 100L);

        verify(jobPostRepository).delete(jobPost);
    }

    @Test
    void closeJobPost_success() {
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));

        jobService.closeJobPost(userDetails, 100L);

        assertFalse(jobPost.getIsActive());
    }

    @Test
    void reopenJobPost_success() {
        jobPost.setIsActive(false);

        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));

        jobService.reopenJobPost(userDetails, 100L);

        assertTrue(jobPost.getIsActive());
    }

    @Test
    void markJobFilled_success() {
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        when(jobPostRepository.findById(100L))
                .thenReturn(Optional.of(jobPost));

        jobService.markJobFilled(userDetails, 100L);

        assertTrue(jobPost.getIsFilled());
        assertFalse(jobPost.getIsActive());
    }

    @Test
    void getMyJobPosts_success() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<JobPost> page = new PageImpl<>(java.util.List.of(jobPost));

        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        when(jobPostRepository.findByEmployerId(employer.getId(), pageable))
                .thenReturn(page);

        Page<JobPostDto> result = jobService.getMyJobPosts(userDetails, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void userNotFound_shouldThrow() {
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> jobService.createJobPost(userDetails, new CreateJobPostRequest()));
    }
}