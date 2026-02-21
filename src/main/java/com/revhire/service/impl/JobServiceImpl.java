package com.revhire.service.impl;

import com.revhire.dto.jobs.CreateJobPostRequest;
import com.revhire.dto.jobs.JobPostDto;
import com.revhire.dto.jobs.JobSearchFilter;
import com.revhire.dto.jobs.UpdateJobPostRequest;
import com.revhire.entity.EmployerProfile;
import com.revhire.entity.JobPost;
import com.revhire.entity.User;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.exception.UnauthorizedException;
import com.revhire.repository.EmployerProfileRepository;
import com.revhire.repository.JobPostRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.JobService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final JobPostRepository jobPostRepository;

    private User getUser(UserDetails userDetails) {
        log.debug("Fetching user with email: {}", userDetails.getUsername());
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userDetails.getUsername());
                    return new NotFoundException("User not found");
                });
    }

    private EmployerProfile getEmployerProfile(User user) {
        if (user.getEmployerProfile() == null) {
            log.error("Employer profile not found for user: {}", user.getEmail());
            throw new NotFoundException("Employer profile not found");
        }
        return user.getEmployerProfile();
    }

    private JobPost getJobPost(Long id) {
        log.debug("Fetching job post with id: {}", id);
        return jobPostRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Job post not found with id: {}", id);
                    return new NotFoundException("Job post not found");
                });
    }

    private void checkOwnership(EmployerProfile employer, JobPost jobPost) {
        if (!jobPost.getEmployer().getId().equals(employer.getId())) {
            log.warn("Unauthorized access attempt by employer {} on job {}",
                    employer.getId(), jobPost.getId());
            throw new UnauthorizedException("Not authorized");
        }
    }

    private JobPostDto mapToDto(JobPost jobPost) {
        JobPostDto dto = new JobPostDto();
        dto.setId(jobPost.getId());
        dto.setTitle(jobPost.getTitle());
        dto.setDescription(jobPost.getDescription());
        dto.setRequiredSkills(jobPost.getRequiredSkills());
        dto.setExperienceRequired(jobPost.getExperienceRequired());
        dto.setEducationRequired(jobPost.getEducationRequired());
        dto.setLocation(jobPost.getLocation());
        dto.setSalaryMin(jobPost.getSalaryMin());
        dto.setSalaryMax(jobPost.getSalaryMax());
        dto.setJobType(jobPost.getJobType());
        dto.setApplicationDeadline(jobPost.getApplicationDeadline());
        dto.setNumberOfOpenings(jobPost.getNumberOfOpenings());
        dto.setIsActive(jobPost.getIsActive());
        dto.setIsFilled(jobPost.getIsFilled());
        dto.setEmployerId(jobPost.getEmployer().getId());
        dto.setCompanyName(jobPost.getEmployer().getCompanyName());
        return dto;
    }

    @Override
    @Transactional
    public JobPostDto createJobPost(UserDetails currentUser, CreateJobPostRequest request) {

        log.info("User {} attempting to create job post",
                currentUser.getUsername());

        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);

        JobPost jobPost = JobPost.builder()
                .employer(employer)
                .title(request.getTitle())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .experienceRequired(request.getExperienceRequired())
                .educationRequired(request.getEducationRequired())
                .location(request.getLocation())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .jobType(request.getJobType())
                .applicationDeadline(request.getApplicationDeadline())
                .numberOfOpenings(request.getNumberOfOpenings())
                .isActive(true)
                .isFilled(false)
                .build();

        jobPost = jobPostRepository.save(jobPost);

        log.info("Job post created successfully with id {} by employer {}",
                jobPost.getId(), employer.getId());

        return mapToDto(jobPost);
    }

    @Override
    public JobPostDto getJobPostById(Long id) {
        log.info("Fetching job post by id {}", id);
        return mapToDto(getJobPost(id));
    }

    @Override
    public Page<JobPostDto> searchJobs(JobSearchFilter filter, Pageable pageable) {

        log.info("Searching jobs with filters: title={}, location={}, company={}, jobType={}",
                filter.getTitle(), filter.getLocation(),
                filter.getCompany(), filter.getJobType());

        Specification<JobPost> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));
            if (filter.getTitle() != null && !filter.getTitle().isEmpty())
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%"));
            if (filter.getLocation() != null && !filter.getLocation().isEmpty())
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + filter.getLocation().toLowerCase() + "%"));
            if (filter.getCompany() != null && !filter.getCompany().isEmpty())
                predicates.add(cb.like(cb.lower(root.get("employer").get("companyName")), "%" + filter.getCompany().toLowerCase() + "%"));
            if (filter.getJobType() != null)
                predicates.add(cb.equal(root.get("jobType"), filter.getJobType()));
            if (filter.getSalaryMin() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("salaryMax"), filter.getSalaryMin()));
            if (filter.getSalaryMax() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("salaryMin"), filter.getSalaryMax()));
            if (filter.getDatePosted() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getDatePosted().atStartOfDay()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return jobPostRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional
    public JobPostDto updateJobPost(UserDetails currentUser, Long jobId, UpdateJobPostRequest request) {

        log.info("User {} attempting to update job post {}",
                currentUser.getUsername(), jobId);

        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);
        JobPost jobPost = getJobPost(jobId);

        checkOwnership(employer, jobPost);

        if (request.getTitle() != null) jobPost.setTitle(request.getTitle());
        if (request.getDescription() != null) jobPost.setDescription(request.getDescription());
        if (request.getRequiredSkills() != null) jobPost.setRequiredSkills(request.getRequiredSkills());
        if (request.getExperienceRequired() != null) jobPost.setExperienceRequired(request.getExperienceRequired());
        if (request.getEducationRequired() != null) jobPost.setEducationRequired(request.getEducationRequired());
        if (request.getLocation() != null) jobPost.setLocation(request.getLocation());
        if (request.getSalaryMin() != null) jobPost.setSalaryMin(request.getSalaryMin());
        if (request.getSalaryMax() != null) jobPost.setSalaryMax(request.getSalaryMax());
        if (request.getJobType() != null) jobPost.setJobType(request.getJobType());
        if (request.getApplicationDeadline() != null) jobPost.setApplicationDeadline(request.getApplicationDeadline());
        if (request.getNumberOfOpenings() != null) jobPost.setNumberOfOpenings(request.getNumberOfOpenings());
        if (request.getIsActive() != null) jobPost.setIsActive(request.getIsActive());
        if (request.getIsFilled() != null) jobPost.setIsFilled(request.getIsFilled());

        jobPost = jobPostRepository.save(jobPost);

        log.info("Job post {} updated successfully", jobPost.getId());

        return mapToDto(jobPost);
    }

    @Override
    @Transactional
    public void deleteJobPost(UserDetails currentUser, Long jobId) {

        log.info("User {} attempting to delete job post {}",
                currentUser.getUsername(), jobId);

        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);
        JobPost jobPost = getJobPost(jobId);

        checkOwnership(employer, jobPost);

        jobPostRepository.delete(jobPost);

        log.info("Job post {} deleted successfully", jobId);
    }

    @Override
    @Transactional
    public void closeJobPost(UserDetails currentUser, Long jobId) {

        log.info("User {} closing job post {}",
                currentUser.getUsername(), jobId);

        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);
        JobPost jobPost = getJobPost(jobId);

        checkOwnership(employer, jobPost);

        jobPost.setIsActive(false);
        jobPostRepository.save(jobPost);

        log.info("Job post {} closed successfully", jobId);
    }

    @Override
    @Transactional
    public void reopenJobPost(UserDetails currentUser, Long jobId) {

        log.info("User {} reopening job post {}",
                currentUser.getUsername(), jobId);

        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);
        JobPost jobPost = getJobPost(jobId);

        checkOwnership(employer, jobPost);

        jobPost.setIsActive(true);
        jobPostRepository.save(jobPost);

        log.info("Job post {} reopened successfully", jobId);
    }

    @Override
    @Transactional
    public void markJobFilled(UserDetails currentUser, Long jobId) {

        log.info("User {} marking job post {} as filled",
                currentUser.getUsername(), jobId);

        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);
        JobPost jobPost = getJobPost(jobId);

        checkOwnership(employer, jobPost);

        jobPost.setIsFilled(true);
        jobPost.setIsActive(false);
        jobPostRepository.save(jobPost);

        log.info("Job post {} marked as filled successfully", jobId);
    }

    @Override
    public Page<JobPostDto> getMyJobPosts(UserDetails currentUser, Pageable pageable) {

        log.info("Fetching job posts for employer {}",
                currentUser.getUsername());

        User user = getUser(currentUser);
        EmployerProfile employer = getEmployerProfile(user);

        return jobPostRepository
                .findByEmployerId(employer.getId(), pageable)
                .map(this::mapToDto);
    }
}