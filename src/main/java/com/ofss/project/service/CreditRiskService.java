package com.ofss.project.service;

import com.ofss.project.dto.request.RiskAssessment;
import com.ofss.project.entity.CreditCardApplication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
public class CreditRiskService {

    public RiskAssessment evaluate(CreditCardApplication application) {

        BigDecimal monthlyIncome = calculateMonthlyIncome(application);

        BigDecimal dti = calculateDti(
                monthlyIncome,
                application.getExistingEmiAmount()
        );

        BigDecimal riskScore = calculateRiskScore(application, dti, monthlyIncome);

        String riskTier = determineRiskTier(riskScore);

        BigDecimal recommendedLimit =
                calculateCreditLimit(
                        monthlyIncome,
                        application.getRequestedCreditLimit(),
                        riskTier
                );

        String recommendation =
                determineRecommendation(riskTier, dti);

        return new RiskAssessment(
                dti,
                riskScore,
                riskTier,
                recommendedLimit,
                recommendation
        );
    }

    /**
     * Converts annual income into monthly income
     * and adds other monthly income if available.
     */
    private BigDecimal calculateMonthlyIncome(
            CreditCardApplication application
    ) {

        if (application.getAnnualIncome() == null ||
                application.getAnnualIncome().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Annual income must be greater than zero"
            );
        }

        BigDecimal monthlyIncome = application.getAnnualIncome()
                .divide(
                        BigDecimal.valueOf(12),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal otherIncome =
                application.getOtherIncome() != null
                        ? application.getOtherIncome()
                        : BigDecimal.ZERO;

        return monthlyIncome.add(otherIncome);
    }

    /**
     * DTI = Existing monthly EMI / Total monthly income
     */
    private BigDecimal calculateDti(
            BigDecimal monthlyIncome,
            BigDecimal existingEmi
    ) {

        if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Monthly income must be greater than zero"
            );
        }

        if (existingEmi == null) {
            existingEmi = BigDecimal.ZERO;
        }

        return existingEmi.divide(
                monthlyIncome,
                4,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Temporary rule-based risk score.
     * Later replace the prediction part with your ML model.
     *
     * Score range: 0.0 - 1.0
     */
    private BigDecimal calculateRiskScore(
            CreditCardApplication application,
            BigDecimal dti,
            BigDecimal monthlyIncome
    ) {

        BigDecimal score = BigDecimal.ZERO;

        // DTI risk
        if (dti.compareTo(new BigDecimal("0.60")) > 0) {
            score = score.add(new BigDecimal("0.50"));

        } else if (dti.compareTo(new BigDecimal("0.40")) > 0) {
            score = score.add(new BigDecimal("0.30"));
        }

        // Requested limit risk
        BigDecimal requestedLimit =
                application.getRequestedCreditLimit();

        if (requestedLimit != null &&
                requestedLimit.compareTo(
                        monthlyIncome.multiply(BigDecimal.valueOf(5))
                ) > 0) {

            score = score.add(new BigDecimal("0.20"));
        }

        // Age risk
        if (application.getDateOfBirth() != null) {

            int age = calculateAge(
                    application.getDateOfBirth()
            );

            if (age < 21) {
                score = score.add(new BigDecimal("0.10"));
            }
        }

        // Cap score at 1.0
        return score.min(BigDecimal.ONE);
    }

    private int calculateAge(LocalDate dateOfBirth) {

        return Period.between(
                dateOfBirth,
                LocalDate.now()
        ).getYears();
    }

    private String determineRiskTier(BigDecimal riskScore) {

        if (riskScore.compareTo(new BigDecimal("0.30")) < 0) {
            return "LOW";
        }

        if (riskScore.compareTo(new BigDecimal("0.60")) < 0) {
            return "MEDIUM";
        }

        return "HIGH";
    }

    private BigDecimal calculateCreditLimit(
            BigDecimal monthlyIncome,
            BigDecimal requestedLimit,
            String riskTier
    ) {

        BigDecimal multiplier;

        switch (riskTier) {
            case "LOW" ->
                    multiplier = BigDecimal.valueOf(5);

            case "MEDIUM" ->
                    multiplier = BigDecimal.valueOf(3);

            default ->
                    multiplier = BigDecimal.ONE;
        }

        BigDecimal calculatedLimit =
                monthlyIncome.multiply(multiplier);

        if (requestedLimit == null) {
            return calculatedLimit;
        }

        return calculatedLimit.min(requestedLimit);
    }

    private String determineRecommendation(
            String riskTier,
            BigDecimal dti
    ) {

        if ("HIGH".equals(riskTier) ||
                dti.compareTo(new BigDecimal("0.60")) > 0) {

            return "REJECT";
        }

        return "APPROVE";
    }
}