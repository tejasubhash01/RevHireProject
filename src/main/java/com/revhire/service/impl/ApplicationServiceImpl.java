package com.revhire.service.impl;

import com.revhire.dto.applications.*;
import com.revhire.entity.*;
import com.revhire.entity.enums.ApplicationStatus;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.exception.UnauthorizedException;
import com.revhire.repository.*;
import com.revhire.service.ApplicationService;
import com.revhire.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final JobPostRepository jobPostRepository;
    private final JobApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private JobSeekerProfile getJobSeekerProfile(User user) {
        if (user.getJobSeekerProfile() == null)
            throw new BadRequestException("Job seeker profile not found");
        return user.getJobSeekerProfile();
    }

    private EmployerProfile getEmployerProfile(User user) {
        if (user.getEmployerProfile() == null)
            throw new NotFoundException("Employer profile not found");
        return user.getEmployerProfile();
    }

    private JobPost getJobPost(Long jobId) {
        return jobPostRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job post not found"));
    }

    private JobApplication getApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    private ApplicationResponse mapToDto(JobApplication app) {
        ApplicationResponse dto = new ApplicationResponse();
        dto.setId(app.getId());
        dto.setJobPostId(app.getJobPost().getId());
        dto.setJobTitle(app.getJobPost().getTitle());
        dto.setCompanyName(app.getJobPost().getEmployer().getCompanyName());
        dto.setJobSeekerId(app.getJobSeeker().getId());
        dto.setJobSeekerName(app.getJobSeeker().getUser().getName());
        dto.setStatus(app.getStatus());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setAppliedDate(app.getAppliedDate());
        return dto;
    }

    @Override
    @Transactional
    public ApplicationResponse applyForJob(UserDetails currentUser, ApplyJobRequest request) {
        User user = getUser(currentUser);
        JobSeekerProfile jobSeeker = getJobSeekerProfile(user);
        JobPost jobPost = getJobPost(request.getJobPostId());

        if (applicationRepository.existsByJobPostIdAndJobSeekerId(jobPost.getId(), jobSeeker.getId()))
            throw new BadRequestException("Already applied");

        if (!jobPost.getIsActive() || jobPost.getIsFilled())
            throw new BadRequestException("Job not accepting applications");

        if (jobPost.getApplicationDeadline() != null && jobPost.getApplicationDeadline().isBefore(LocalDateTime.now().toLocalDate()))
            throw new BadRequestException("Deadline passed");

        JobApplication application = JobApplication.builder()
                .jobPost(jobPost)
                .jobSeeker(jobSeeker)
                .status(ApplicationStatus.APPLIED)
                .coverLetter(request.getCoverLetter())
                .appliedDate(LocalDateTime.now())
                .build();
        application = applicationRepository.save(application);

        notificationService.createNotification(jobPost.getEmployer().getUser(),
                "New application for " + jobPost.getTitle(), "APPLICATION_UPDATE",
                "/employer/jobs/" + jobPost.getId() + "/applications");
        return mapToDto(application);
    }

    @Override
    public List<ApplicationResponse> getMyApplications(UserDetails currentUser) {
        User user = getUser(currentUser);
        JobSeekerProfile jobSeeker = getJobSeekerProfile(user);
        return applicationRepository.findByJobSeekerId(jobSeeker.getId()).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void withdrawApplication(UserDetails currentUser, Long applicationId, WithdrawApplicationRequest request) {
        User user = getUser(currentUser);
        JobSeekerProfile jobSeeker = getJobSeekerProfile(user);
        JobApplication application = getApplication(applicationId);
        if (!application.getJobSeeker().getId().equals(jobSeeker.getId()))
            throw new UnauthorizedException("Not authorized");
        if (application.getStatus() == ApplicationStatus.WITHDRAWN)
            throw new BadRequestException("Already withdrawn");
        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
        notificationService.createNotification(application.getJobPost().getEmployer().getUser(),
                "Application withdrawn by " + user.getName(), "APPLICATION_UPDATE",
                "/employer/jobs/" + application.getJobPost().getId() + "/applications");
    }

    @Override
    public List<ApplicationResponse> getApplicationsForJob(UserDetails currentUser, Long jobId) {
        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);
        JobPost jobPost = getJobPost(jobId);
        if (!jobPost.getEmployer().getId().equals(employer.getId()))
            throw new UnauthorizedException("Not authorized");
        return applicationRepository.findByJobPostId(jobId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(UserDetails currentUser, Long applicationId,
                                                       UpdateApplicationStatusRequest request) {
        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);
        JobApplication application = getApplication(applicationId);
        if (!application.getJobPost().getEmployer().getId().equals(employer.getId()))
            throw new UnauthorizedException("Not authorized");
        application.setStatus(request.getStatus());
        if (request.getEmployerNotes() != null)
            application.setEmployerNotes(request.getEmployerNotes());
        application = applicationRepository.save(application);
        notificationService.createNotification(application.getJobSeeker().getUser(),
                "Your application for " + application.getJobPost().getTitle() + " is now " + request.getStatus(),
                "APPLICATION_UPDATE", "/jobseeker/applications");
        return mapToDto(application);
    }

    @Override
    @Transactional
    public ApplicationResponse addNote(UserDetails currentUser, Long applicationId, NoteRequest request) {
        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);
        JobApplication application = getApplication(applicationId);
        if (!application.getJobPost().getEmployer().getId().equals(employer.getId()))
            throw new UnauthorizedException("Not authorized");
        application.setEmployerNotes(request.getNote());
        application = applicationRepository.save(application);
        return mapToDto(application);
    }
}