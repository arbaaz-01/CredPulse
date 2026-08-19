package com.ofss.project.service;

import com.ofss.project.dto.request.RiskAssessment;
import com.ofss.project.dto.response.ApplicationRiskResponse;
import com.ofss.project.dto.response.ApplicationDocumentResponse;
import com.ofss.project.dto.response.CardIssuanceResponse;
import com.ofss.project.entity.ApplicationDocument;
import com.ofss.project.entity.CreditCardApplication;
import com.ofss.project.enums.ApplicationStatus;
import com.ofss.project.repository.CreditCardApplicationRepository;
import com.ofss.project.repository.ApplicationDocumentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditOfficerService {

    private final CreditCardApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository documentRepository;
    private final CreditRiskService creditRiskService;
    private final CreditCardService creditCardService;
   

    public List<ApplicationRiskResponse> getAllApplications() {

        return applicationRepository
                .findByStatusIn(
                        List.of(
                                ApplicationStatus.SUBMITTED,
                                ApplicationStatus.UNDER_REVIEW,
                                ApplicationStatus.APPROVED
                        )
                )
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

    public List<ApplicationDocumentResponse> getApplicationDocuments(Long applicationId) {

        getOfficerAccessibleApplication(applicationId);

        return documentRepository
                .findByApplication_IdOrderByUploadedAtAsc(applicationId)
                .stream()
                .map(this::toDocumentResponse)
                .toList();
    }

    public CardIssuanceResponse approveApplication(Long id) {

    CreditCardApplication application =
            getDecisionReadyApplication(id);

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
                        getDecisionReadyApplication(id);

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
                        application.getStatus(),

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

   private ApplicationDocumentResponse toDocumentResponse(
           ApplicationDocument document
   ) {

       return new ApplicationDocumentResponse(
               document.getId(),
               document.getDocumentType(),
               document.getOriginalFileName(),
               document.getContentType(),
               document.getFileSize(),
               document.getVerificationStatus(),
               document.getUploadedAt(),
               document.getVerifiedAt()
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

private CreditCardApplication getDecisionReadyApplication(Long id) {

        CreditCardApplication application = getOfficerAccessibleApplication(id);

        if (application.getStatus() != ApplicationStatus.SUBMITTED
                && application.getStatus() != ApplicationStatus.UNDER_REVIEW) {

            throw new IllegalStateException(
                    "Only submitted applications can be approved or rejected"
            );
        }

        return application;
}
}
