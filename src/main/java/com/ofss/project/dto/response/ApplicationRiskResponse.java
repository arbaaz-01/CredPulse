package com.ofss.project.dto.response;

import com.ofss.project.dto.request.RiskAssessment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApplicationRiskResponse(

        Long applicationId,
        String applicationNumber,

        String customerName,

        LocalDate dateOfBirth,

        String employmentType,
        String employerName,

        BigDecimal annualIncome,
        BigDecimal monthlyExpenses,
        BigDecimal existingLoanAmount,
        BigDecimal existingEmiAmount,
        BigDecimal otherIncome,

        BigDecimal requestedCreditLimit,

        RiskAssessment riskAssessment

) {
}