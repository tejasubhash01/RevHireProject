package com.revhire.service.impl;

import com.revhire.dto.jobseeker.*;
import com.revhire.entity.JobSeekerProfile;
import com.revhire.entity.ResumeFile;
import com.revhire.entity.ResumeText;
import com.revhire.entity.User;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.JobSeekerProfileRepository;
import com.revhire.repository.ResumeFileRepository;
import com.revhire.repository.ResumeTextRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.JobSeekerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobSeekerServiceImpl implements JobSeekerService {
    private static final Logger log = LoggerFactory.getLogger(JobSeekerServiceImpl.class);

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository profileRepository;
    private final ResumeTextRepository resumeTextRepository;
    private final ResumeFileRepository resumeFileRepository;

    @Value("${file.upload-dir:./uploads/resumes}")
    private String uploadDir;

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found: {}", userDetails.getUsername());
                    return new NotFoundException("User not found");
                });
    }

    private JobSeekerProfile getProfile(User user) {
        if (user.getJobSeekerProfile() == null) {
            log.warn("Job seeker profile not found for user: {}", user.getEmail());
            throw new NotFoundException("Job seeker profile not found");
        }
        return user.getJobSeekerProfile();
    }

    private JobSeekerProfileDto mapToDto(JobSeekerProfile profile) {
        JobSeekerProfileDto dto = new JobSeekerProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setName(profile.getUser().getName());
        dto.setEmail(profile.getUser().getEmail());
        dto.setPhone(profile.getUser().getPhone());
        dto.setLocation(profile.getUser().getLocation());
        dto.setEmploymentStatus(profile.getEmploymentStatus());
        dto.setHeadline(profile.getHeadline());
        dto.setSummary(profile.getSummary());
        if (profile.getResumeText() != null) {
            ResumeTextDto textDto = new ResumeTextDto();
            textDto.setId(profile.getResumeText().getId());
            textDto.setObjective(profile.getResumeText().getObjective());
            textDto.setEducation(profile.getResumeText().getEducation());
            textDto.setExperience(profile.getResumeText().getExperience());
            textDto.setSkills(profile.getResumeText().getSkills());
            textDto.setProjects(profile.getResumeText().getProjects());
            textDto.setCertifications(profile.getResumeText().getCertifications());
            dto.setResumeText(textDto);
        }
        if (profile.getResumeFile() != null) {
            ResumeFileDto fileDto = new ResumeFileDto();
            fileDto.setId(profile.getResumeFile().getId());
            fileDto.setFileName(profile.getResumeFile().getFileName());
            fileDto.setFileType(profile.getResumeFile().getFileType());
            fileDto.setFileSize(profile.getResumeFile().getFileSize());
            fileDto.setUploadDate(profile.getResumeFile().getUploadDate());
            dto.setResumeFile(fileDto);
        }
        return dto;
    }

    @Override
    @Transactional
    public JobSeekerProfileDto createProfile(UserDetails currentUser, CreateJobSeekerProfileRequest request) {
        User user = getUser(currentUser);
        log.info("Creating job seeker profile for user: {}", user.getEmail());
        if (user.getJobSeekerProfile() != null) {
            log.warn("Profile already exists for user: {}", user.getEmail());
            throw new BadRequestException("Profile already exists");
        }
        JobSeekerProfile profile = JobSeekerProfile.builder()
                .user(user)
                .employmentStatus(request.getEmploymentStatus())
                .headline(request.getHeadline())
                .summary(request.getSummary())
                .build();
        profile = profileRepository.save(profile);
        log.info("Job seeker profile created with id: {} for user: {}", profile.getId(), user.getEmail());
        return mapToDto(profile);
    }

    @Override
    public JobSeekerProfileDto getMyProfile(UserDetails currentUser) {
        User user = getUser(currentUser);
        log.debug("Fetching profile for user: {}", user.getEmail());
        JobSeekerProfile profile = getProfile(user);
        return mapToDto(profile);
    }

    @Override
    @Transactional
    public JobSeekerProfileDto updateProfile(UserDetails currentUser, CreateJobSeekerProfileRequest request) {
        User user = getUser(currentUser);
        log.info("Updating profile for user: {}", user.getEmail());
        JobSeekerProfile profile = getProfile(user);
        if (request.getEmploymentStatus() != null) profile.setEmploymentStatus(request.getEmploymentStatus());
        if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
        if (request.getSummary() != null) profile.setSummary(request.getSummary());
        profile = profileRepository.save(profile);
        log.info("Profile updated for user: {}", user.getEmail());
        return mapToDto(profile);
    }

    @Override
    @Transactional
    public ResumeTextDto createOrUpdateResumeText(UserDetails currentUser, UpdateResumeTextRequest request) {
        User user = getUser(currentUser);
        log.info("Updating resume text for user: {}", user.getEmail());
        JobSeekerProfile profile = getProfile(user);
        ResumeText resumeText = profile.getResumeText();
        if (resumeText == null) {
            resumeText = ResumeText.builder()
                    .jobSeekerProfile(profile)
                    .objective(request.getObjective())
                    .education(request.getEducation())
                    .experience(request.getExperience())
                    .skills(request.getSkills())
                    .projects(request.getProjects())
                    .certifications(request.getCertifications())
                    .build();
        } else {
            if (request.getObjective() != null) resumeText.setObjective(request.getObjective());
            if (request.getEducation() != null) resumeText.setEducation(request.getEducation());
            if (request.getExperience() != null) resumeText.setExperience(request.getExperience());
            if (request.getSkills() != null) resumeText.setSkills(request.getSkills());
            if (request.getProjects() != null) resumeText.setProjects(request.getProjects());
            if (request.getCertifications() != null) resumeText.setCertifications(request.getCertifications());
        }
        resumeText = resumeTextRepository.save(resumeText);
        profile.setResumeText(resumeText);
        profileRepository.save(profile);
        log.info("Resume text saved for user: {}", user.getEmail());
        ResumeTextDto dto = new ResumeTextDto();
        dto.setId(resumeText.getId());
        dto.setObjective(resumeText.getObjective());
        dto.setEducation(resumeText.getEducation());
        dto.setExperience(resumeText.getExperience());
        dto.setSkills(resumeText.getSkills());
        dto.setProjects(resumeText.getProjects());
        dto.setCertifications(resumeText.getCertifications());
        return dto;
    }

    @Override
    public ResumeTextDto getMyResumeText(UserDetails currentUser) {
        User user = getUser(currentUser);
        log.debug("Fetching resume text for user: {}", user.getEmail());
        JobSeekerProfile profile = getProfile(user);
        if (profile.getResumeText() == null) {
            log.warn("Resume text not found for user: {}", user.getEmail());
            throw new NotFoundException("Resume text not found");
        }
        ResumeText resumeText = profile.getResumeText();
        ResumeTextDto dto = new ResumeTextDto();
        dto.setId(resumeText.getId());
        dto.setObjective(resumeText.getObjective());
        dto.setEducation(resumeText.getEducation());
        dto.setExperience(resumeText.getExperience());
        dto.setSkills(resumeText.getSkills());
        dto.setProjects(resumeText.getProjects());
        dto.setCertifications(resumeText.getCertifications());
        return dto;
    }

    @Override
    @Transactional
    public ResumeFileDto uploadResumeFile(UserDetails currentUser, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.warn("Empty file upload attempted");
            throw new BadRequestException("File is empty");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            log.warn("File size exceeds 2MB: {} bytes", file.getSize());
            throw new BadRequestException("File size exceeds 2MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            log.warn("Invalid file type: {}", contentType);
            throw new BadRequestException("Only PDF and DOCX files are allowed");
        }

        User user = getUser(currentUser);
        log.info("Uploading resume file for user: {}", user.getEmail());
        JobSeekerProfile profile = getProfile(user);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        ResumeFile resumeFile = profile.getResumeFile();
        if (resumeFile == null) {
            resumeFile = ResumeFile.builder()
                    .jobSeekerProfile(profile)
                    .fileName(originalFilename)
                    .fileType(contentType)
                    .filePath(filePath.toString())
                    .fileSize(file.getSize())
                    .uploadDate(LocalDateTime.now())
                    .build();
        } else {
            Path oldPath = Paths.get(resumeFile.getFilePath());
            try {
                Files.deleteIfExists(oldPath);
                log.debug("Deleted old resume file: {}", oldPath);
            } catch (IOException e) {
                log.warn("Could not delete old resume file: {}", oldPath);
            }
            resumeFile.setFileName(originalFilename);
            resumeFile.setFileType(contentType);
            resumeFile.setFilePath(filePath.toString());
            resumeFile.setFileSize(file.getSize());
            resumeFile.setUploadDate(LocalDateTime.now());
        }
        resumeFile = resumeFileRepository.save(resumeFile);
        profile.setResumeFile(resumeFile);
        profileRepository.save(profile);
        log.info("Resume file uploaded for user: {}, file: {}", user.getEmail(), filename);

        ResumeFileDto dto = new ResumeFileDto();
        dto.setId(resumeFile.getId());
        dto.setFileName(resumeFile.getFileName());
        dto.setFileType(resumeFile.getFileType());
        dto.setFileSize(resumeFile.getFileSize());
        dto.setUploadDate(resumeFile.getUploadDate());
        return dto;
    }

    @Override
    public ResumeFileDto getMyResumeFile(UserDetails currentUser) {
        User user = getUser(currentUser);
        log.debug("Fetching resume file metadata for user: {}", user.getEmail());
        JobSeekerProfile profile = getProfile(user);
        if (profile.getResumeFile() == null) {
            log.warn("Resume file not found for user: {}", user.getEmail());
            throw new NotFoundException("Resume file not found");
        }
        ResumeFile resumeFile = profile.getResumeFile();
        ResumeFileDto dto = new ResumeFileDto();
        dto.setId(resumeFile.getId());
        dto.setFileName(resumeFile.getFileName());
        dto.setFileType(resumeFile.getFileType());
        dto.setFileSize(resumeFile.getFileSize());
        dto.setUploadDate(resumeFile.getUploadDate());
        return dto;
    }
}