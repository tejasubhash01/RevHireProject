package com.revhire.service;

import com.revhire.dto.jobseeker.CreateJobSeekerProfileRequest;
import com.revhire.dto.jobseeker.UpdateResumeTextRequest;
import com.revhire.entity.JobSeekerProfile;
import com.revhire.entity.ResumeFile;
import com.revhire.entity.ResumeText;
import com.revhire.entity.User;
import com.revhire.entity.enums.EmploymentStatus;
import com.revhire.entity.enums.Role;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.JobSeekerProfileRepository;
import com.revhire.repository.ResumeFileRepository;
import com.revhire.repository.ResumeTextRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.impl.JobSeekerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JobSeekerProfileRepository profileRepository;
    @Mock
    private ResumeTextRepository resumeTextRepository;
    @Mock
    private ResumeFileRepository resumeFileRepository;

    @InjectMocks
    private JobSeekerServiceImpl jobSeekerService;

    private UserDetails mockUserDetails;
    private User mockUser;
    private JobSeekerProfile mockProfile;
    private ResumeText mockResumeText;
    private ResumeFile mockResumeFile;

    @BeforeEach
    void setUp() {
        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("seeker@test.com")
                .password("pass")
                .roles("JOB_SEEKER")
                .build();

        mockUser = User.builder()
                .email("seeker@test.com")
                .name("Test Seeker")
                .role(Role.JOB_SEEKER)
                .build();
        mockUser.setId(1L);

        mockProfile = JobSeekerProfile.builder()
                .user(mockUser)
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .headline("Developer")
                .summary("Experienced")
                .build();
        mockProfile.setId(10L);
        mockUser.setJobSeekerProfile(mockProfile);

        mockResumeText = ResumeText.builder()
                .jobSeekerProfile(mockProfile)
                .objective("Objective")
                .build();
        mockResumeText.setId(100L);
        mockProfile.setResumeText(mockResumeText);

        mockResumeFile = ResumeFile.builder()
                .jobSeekerProfile(mockProfile)
                .fileName("resume.pdf")
                .fileType("application/pdf")
                .fileSize(1024L)
                .build();
        mockResumeFile.setId(200L);
        mockProfile.setResumeFile(mockResumeFile);
    }

    @Test
    void getMyProfile_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> jobSeekerService.getMyProfile(mockUserDetails));
    }

    @Test
    void getMyProfile_WhenProfileNotFound_ThrowsNotFoundException() {
        mockUser.setJobSeekerProfile(null);
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        assertThrows(NotFoundException.class, () -> jobSeekerService.getMyProfile(mockUserDetails));
    }

    @Test
    void getMyProfile_ReturnsProfile() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        var result = jobSeekerService.getMyProfile(mockUserDetails);
        assertNotNull(result);
        assertEquals(mockProfile.getId(), result.getId());
    }

    @Test
    void createProfile_WhenProfileAlreadyExists_ThrowsBadRequest() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        CreateJobSeekerProfileRequest request = new CreateJobSeekerProfileRequest();
        request.setHeadline("New Headline");
        request.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        assertThrows(BadRequestException.class, () -> jobSeekerService.createProfile(mockUserDetails, request));
    }

    @Test
    void createProfile_CreatesProfile() {
        mockUser.setJobSeekerProfile(null);
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(profileRepository.save(any(JobSeekerProfile.class))).thenAnswer(inv -> {
            JobSeekerProfile saved = inv.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        CreateJobSeekerProfileRequest request = new CreateJobSeekerProfileRequest();
        request.setHeadline("Developer");
        request.setSummary("Summary");
        request.setEmploymentStatus(EmploymentStatus.EMPLOYED);

        var result = jobSeekerService.createProfile(mockUserDetails, request);
        assertNotNull(result);
        assertEquals("Developer", result.getHeadline());
        verify(profileRepository, times(1)).save(any());
    }

    @Test
    void updateProfile_UpdatesProfile() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(profileRepository.save(any(JobSeekerProfile.class))).thenReturn(mockProfile);

        CreateJobSeekerProfileRequest request = new CreateJobSeekerProfileRequest();
        request.setHeadline("Senior Developer");
        request.setSummary("Updated summary");
        request.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);

        var result = jobSeekerService.updateProfile(mockUserDetails, request);
        assertEquals("Senior Developer", result.getHeadline());
        verify(profileRepository, times(1)).save(mockProfile);
    }

    @Test
    void createOrUpdateResumeText_CreatesNewWhenNotExists() {
        mockProfile.setResumeText(null);
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(resumeTextRepository.save(any(ResumeText.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateResumeTextRequest request = new UpdateResumeTextRequest();
        request.setObjective("Objective");
        request.setEducation("Education");
        request.setExperience("Experience");
        request.setSkills("Skills");
        request.setProjects("Projects");
        request.setCertifications("Certifications");

        var result = jobSeekerService.createOrUpdateResumeText(mockUserDetails, request);
        assertNotNull(result);
        assertEquals("Objective", result.getObjective());
        verify(resumeTextRepository, times(1)).save(any());
    }

    @Test
    void createOrUpdateResumeText_UpdatesExisting() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(resumeTextRepository.save(any(ResumeText.class))).thenReturn(mockResumeText);

        UpdateResumeTextRequest request = new UpdateResumeTextRequest();
        request.setObjective("New Objective");

        var result = jobSeekerService.createOrUpdateResumeText(mockUserDetails, request);
        assertEquals("New Objective", result.getObjective());
        verify(resumeTextRepository, times(1)).save(mockResumeText);
    }

    @Test
    void getMyResumeText_ReturnsText() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        var result = jobSeekerService.getMyResumeText(mockUserDetails);
        assertEquals(mockResumeText.getObjective(), result.getObjective());
    }

    @Test
    void getMyResumeText_NotFound_ThrowsNotFoundException() {
        mockProfile.setResumeText(null);
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        assertThrows(NotFoundException.class, () -> jobSeekerService.getMyResumeText(mockUserDetails));
    }





    @Test
    void getMyResumeFile_ReturnsMetadata() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        var result = jobSeekerService.getMyResumeFile(mockUserDetails);
        assertEquals(mockResumeFile.getFileName(), result.getFileName());
    }

    @Test
    void getMyResumeFile_NotFound_ThrowsNotFoundException() {
        mockProfile.setResumeFile(null);
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        assertThrows(NotFoundException.class, () -> jobSeekerService.getMyResumeFile(mockUserDetails));
    }
}