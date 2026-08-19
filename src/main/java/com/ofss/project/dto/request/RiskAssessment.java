package com.ofss.project.dto.request;

import java.math.BigDecimal;

public record RiskAssessment(
        BigDecimal dti,
        BigDecimal riskScore,
        String riskTier,
        BigDecimal recommendedLimit,
        String recommendation
) {
}