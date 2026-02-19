package com.revhire.dto.applications;

import com.revhire.entity.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {
    @NotNull private ApplicationStatus status;
    private String employerNotes;
}