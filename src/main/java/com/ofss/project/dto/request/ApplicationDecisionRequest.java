package com.ofss.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationDecisionRequest(

        @NotBlank(message = "Remark is required")
        @Size(max = 500, message = "Remark cannot exceed 500 characters")
        String remark

) {
}