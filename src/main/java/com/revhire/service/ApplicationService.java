package com.revhire.service;

import com.revhire.dto.applications.*;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyForJob(UserDetails currentUser, ApplyJobRequest request);
    List<ApplicationResponse> getMyApplications(UserDetails currentUser);
    void withdrawApplication(UserDetails currentUser, Long applicationId, WithdrawApplicationRequest request);
    List<ApplicationResponse> getApplicationsForJob(UserDetails currentUser, Long jobId);
    ApplicationResponse updateApplicationStatus(UserDetails currentUser, Long applicationId, UpdateApplicationStatusRequest request);
    ApplicationResponse addNote(UserDetails currentUser, Long applicationId, NoteRequest request);
}