package com.revhire.service;

import com.revhire.dto.jobseeker.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface JobSeekerService {
    JobSeekerProfileDto createProfile(UserDetails currentUser, CreateJobSeekerProfileRequest request);
    JobSeekerProfileDto getMyProfile(UserDetails currentUser);
    JobSeekerProfileDto updateProfile(UserDetails currentUser, CreateJobSeekerProfileRequest request);
    ResumeTextDto createOrUpdateResumeText(UserDetails currentUser, UpdateResumeTextRequest request);
    ResumeTextDto getMyResumeText(UserDetails currentUser);
    ResumeFileDto uploadResumeFile(UserDetails currentUser, MultipartFile file) throws IOException;
    ResumeFileDto getMyResumeFile(UserDetails currentUser);
}