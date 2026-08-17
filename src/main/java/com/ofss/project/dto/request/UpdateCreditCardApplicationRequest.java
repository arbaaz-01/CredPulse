package com.ofss.project.dto.request;

import com.ofss.project.enums.EmploymentType;
import com.ofss.project.enums.Gender;
import com.ofss.project.enums.MaritalStatus;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCreditCardApplicationRequest(

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotNull(message = "Marital status is required")
        MaritalStatus maritalStatus,

        @NotBlank(message = "Address line 1 is required")
        @Size(max = 200)
        String addressLine1,

        @Size(max = 200)
        String addressLine2,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 100)
        String state,

        @NotBlank(message = "Postal code is required")
        @Size(max = 20)
        String postalCode,

        @NotBlank(message = "Country is required")
        @Size(max = 100)
        String country,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType,

        @Size(max = 150)
        String employerName,

        @Size(max = 100)
        String designation,

        @DecimalMin(
                value = "0.0",
                message = "Years of experience cannot be negative"
        )
        BigDecimal yearsOfExperience,

        @NotNull(message = "Annual income is required")
        @DecimalMin(
                value = "0.0",
                message = "Annual income cannot be negative"
        )
        BigDecimal annualIncome,

        @NotNull(message = "Monthly expenses are required")
        @DecimalMin(
                value = "0.0",
                message = "Monthly expenses cannot be negative"
        )
        BigDecimal monthlyExpenses,

        @DecimalMin(
                value = "0.0",
                message = "Existing loan amount cannot be negative"
        )
        BigDecimal existingLoanAmount,

        @DecimalMin(
                value = "0.0",
                message = "Existing EMI amount cannot be negative"
        )
        BigDecimal existingEmiAmount,

        @DecimalMin(
                value = "0.0",
                message = "Other income cannot be negative"
        )
        BigDecimal otherIncome,

        @NotNull(message = "Requested credit limit is required")
        @DecimalMin(
                value = "1.00",
                message = "Requested credit limit must be greater than zero"
        )
        BigDecimal requestedCreditLimit,

        @AssertTrue(message = "Consent must be given")
        boolean consentGiven,

        @AssertTrue(message = "Declaration must be accepted")
        boolean declarationAccepted
) {
}