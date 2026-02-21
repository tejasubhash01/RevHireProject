package com.revhire.service;

import com.revhire.dto.jobseeker.*;
import com.revhire.entity.*;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.*;
import com.revhire.service.impl.JobSeekerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class JobSeekerServiceImplTest {

    @InjectMocks
    private JobSeekerServiceImpl service;

    @Mock private UserRepository userRepository;
    @Mock private JobSeekerProfileRepository profileRepository;
    @Mock private ResumeTextRepository resumeTextRepository;
    @Mock private ResumeFileRepository resumeFileRepository;

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setName("Test User");

        userDetails = mock(UserDetails.class);

     
        lenient().when(userDetails.getUsername()).thenReturn("test@gmail.com");
    }



    @Test
    void testCreateProfile_Success() {
        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(profileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CreateJobSeekerProfileRequest request = new CreateJobSeekerProfileRequest();
        request.setHeadline("Java Dev");

        JobSeekerProfileDto result = service.createProfile(userDetails, request);

        assertNotNull(result);
        assertEquals("Java Dev", result.getHeadline());
    }

    @Test
    void testCreateProfile_AlreadyExists() {
        JobSeekerProfile profile = new JobSeekerProfile();
        user.setJobSeekerProfile(profile);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> service.createProfile(userDetails, new CreateJobSeekerProfileRequest()));
    }

   

    @Test
    void testGetMyProfile_Success() {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUser(user);
        user.setJobSeekerProfile(profile);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        JobSeekerProfileDto dto = service.getMyProfile(userDetails);

        assertNotNull(dto);
        assertEquals("Test User", dto.getName());
    }


    @Test
    void testUpdateProfile_Success() {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUser(user);
        user.setJobSeekerProfile(profile);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));
        when(profileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CreateJobSeekerProfileRequest request = new CreateJobSeekerProfileRequest();
        request.setHeadline("Updated");

        JobSeekerProfileDto result =
                service.updateProfile(userDetails, request);

        assertEquals("Updated", result.getHeadline());
    }

   

    @Test
    void testCreateOrUpdateResumeText_Success() {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUser(user);
        user.setJobSeekerProfile(profile);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));
        when(resumeTextRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(profileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateResumeTextRequest request = new UpdateResumeTextRequest();
        request.setObjective("Backend Dev");

        ResumeTextDto dto =
                service.createOrUpdateResumeText(userDetails, request);

        assertEquals("Backend Dev", dto.getObjective());
    }

    @Test
    void testGetMyResumeText_NotFound() {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUser(user);
        user.setJobSeekerProfile(profile);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class,
                () -> service.getMyResumeText(userDetails));
    }


    @Test
    void testUploadResumeFile_Success() throws IOException {

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUser(user);
        user.setJobSeekerProfile(profile);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(resumeFileRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(profileRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        ResumeFileDto dto = service.uploadResumeFile(userDetails, file);

        assertNotNull(dto);
        assertEquals("resume.pdf", dto.getFileName());
    }

    @Test
    void testUploadResumeFile_InvalidSize() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                new byte[3 * 1024 * 1024]
        );

        assertThrows(BadRequestException.class,
                () -> service.uploadResumeFile(userDetails, file));
    }


    @Test
    void testGetMyResumeFile_NotFound() {

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUser(user);
        user.setJobSeekerProfile(profile);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class,
                () -> service.getMyResumeFile(userDetails));
    }
}