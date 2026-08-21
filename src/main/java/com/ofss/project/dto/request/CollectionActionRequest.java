package com.ofss.project.dto.request;

import com.ofss.project.enums.CollectionActionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CollectionActionRequest(

        @NotNull
        CollectionActionType actionType,

        @Size(
                max = 500,
                message = "Remarks cannot exceed 500 characters"
        )
        String remarks

) {
}