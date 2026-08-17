package com.ofss.project.dto.response;

import com.ofss.project.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreditCardApplicationResponse(

        Long id,
        String applicationNumber,

        Long productId,
        String productCode,
        String productName,

        String applicantName,
        String applicantEmail,
        String applicantMobile,

        ApplicationStatus status,

        LocalDate dateOfBirth,
        Gender gender,
        MaritalStatus maritalStatus,

        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,

        EmploymentType employmentType,
        String employerName,
        String designation,
        BigDecimal yearsOfExperience,

        BigDecimal annualIncome,
        BigDecimal monthlyExpenses,
        BigDecimal existingLoanAmount,
        BigDecimal existingEmiAmount,
        BigDecimal otherIncome,
        BigDecimal requestedCreditLimit,

        boolean consentGiven,
        boolean declarationAccepted,

        LocalDateTime submittedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}