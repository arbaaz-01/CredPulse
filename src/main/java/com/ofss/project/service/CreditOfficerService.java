package com.ofss.project.service;

import com.ofss.project.dto.request.RiskAssessment;
import com.ofss.project.dto.response.ApplicationRiskResponse;
import com.ofss.project.dto.response.CardIssuanceResponse;
import com.ofss.project.entity.CreditCardApplication;
import com.ofss.project.enums.ApplicationStatus;
import com.ofss.project.repository.CreditCardApplicationRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditOfficerService {

    private final CreditCardApplicationRepository applicationRepository;
    private final CreditRiskService creditRiskService;
    private final CreditCardService creditCardService;
   

    public List<ApplicationRiskResponse> getAllApplications() {

        return applicationRepository
                .findByStatusNot(ApplicationStatus.DRAFT)
                .stream()
                .map(this::toRiskResponse)
                .toList();
    }

    public ApplicationRiskResponse getApplicationDetails(Long id) {

        CreditCardApplication application =
                applicationRepository.findByIdAndStatusNot(
                            id,
                            ApplicationStatus.DRAFT
                    ) .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found: " + id
                                ));

        return toRiskResponse(application);
    }

    public CardIssuanceResponse approveApplication(Long id) {

    CreditCardApplication application =
            getOfficerAccessibleApplication(id);

    RiskAssessment assessment =
            creditRiskService.evaluate(application);

    if (!"APPROVE".equals(assessment.recommendation())) {
        throw new IllegalStateException(
                "Application does not meet approval criteria"
        );
    }

    application.setStatus(
            ApplicationStatus.APPROVED
    );

    applicationRepository.save(application);

    return creditCardService.issueCard(
            application,
            assessment.recommendedLimit()
    );
}

        public void rejectApplication(Long id) {

                CreditCardApplication application =
                        getOfficerAccessibleApplication(id);

                application.setStatus(ApplicationStatus.REJECTED);

                applicationRepository.save(application);
        }

   private ApplicationRiskResponse toRiskResponse(
                 CreditCardApplication application
        ) {

                RiskAssessment assessment =
                        creditRiskService.evaluate(application);

                return new ApplicationRiskResponse(
                        application.getId(),
                        application.getApplicationNumber(),

                        application.getUser().getName(),

                        application.getDateOfBirth(),

                        application.getEmploymentType() != null
                                ? application.getEmploymentType().name()
                                : null,

                        application.getEmployerName(),

                        application.getAnnualIncome(),
                        application.getMonthlyExpenses(),
                        application.getExistingLoanAmount(),
                        application.getExistingEmiAmount(),
                        application.getOtherIncome(),

                        application.getRequestedCreditLimit(),

                        assessment
                );
        }
private CreditCardApplication getOfficerAccessibleApplication(Long id) {

        return applicationRepository
                .findByIdAndStatusNot(
                        id,
                        ApplicationStatus.DRAFT
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found or still in draft"
                        )
                );
        }
}