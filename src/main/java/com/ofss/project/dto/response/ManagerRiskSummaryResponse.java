package com.ofss.project.dto.response;

import java.math.BigDecimal;

public record ManagerRiskSummaryResponse(

        BigDecimal totalOutstanding,

        BigDecimal npaAmount,

        BigDecimal npaRatio,

        long standardAccounts,
        BigDecimal standardAmount,

        long sma0Accounts,
        BigDecimal sma0Amount,

        long sma1Accounts,
        BigDecimal sma1Amount,

        long sma2Accounts,
        BigDecimal sma2Amount,

        long substandardAccounts,
        BigDecimal substandardAmount,

        long doubtfulAccounts,
        BigDecimal doubtfulAmount,

        long lossAccounts,
        BigDecimal lossAmount

) {
}