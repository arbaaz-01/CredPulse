package com.ofss.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.project.dto.request.CreateCreditCardApplicationRequest;
import com.ofss.project.dto.request.UpdateCreditCardApplicationRequest;
import com.ofss.project.dto.response.CreditCardApplicationResponse;
import com.ofss.project.entity.ApplicationStatusHistory;
import com.ofss.project.entity.CreditCardApplication;
import com.ofss.project.entity.CreditCardProduct;
import com.ofss.project.entity.User;
import com.ofss.project.enums.ApplicationStatus;
import com.ofss.project.enums.CreditCardProductStatus;
import com.ofss.project.enums.EmploymentType;
import com.ofss.project.exception.UserNotFoundException;
import com.ofss.project.repository.ApplicationStatusHistoryRepository;
import com.ofss.project.repository.CreditCardApplicationRepository;
import com.ofss.project.repository.CreditCardProductRepository;
import com.ofss.project.repository.UserRepository;
import com.ofss.project.security.CurrentUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditCardApplicationService {

    private final CreditCardApplicationRepository applicationRepository;
    private final CreditCardProductRepository productRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    @Transactional
    public CreditCardApplicationResponse createDraft(
            CreateCreditCardApplicationRequest request) {

        Long userId =
                currentUser.getUserId();

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found"
                                )
                        );

        CreditCardProduct product =
                productRepository.findByIdAndStatus(
                                request.productId(),
                                CreditCardProductStatus.ACTIVE
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Credit card product not found"
                                )
                        );

        validateRequestedLimit(
                request.requestedCreditLimit(),
                product
        );

        CreditCardApplication application =
                CreditCardApplication.builder()
                        .user(user)
                        .product(product)
                        .status(ApplicationStatus.DRAFT)
                        .requestedCreditLimit(
                                request.requestedCreditLimit()
                        )
                        .consentGiven(false)
                        .declarationAccepted(false)
                        .build();

        application = applicationRepository.save(application);

        application.setApplicationNumber(
                buildApplicationNumber(application.getId())
        );

        application = applicationRepository.save(application);

        saveStatusHistory(
                application,
                null,
                ApplicationStatus.DRAFT,
                user,
                "Application draft created"
        );

        return toResponse(application);
    }

    @Transactional(readOnly = true)
    public List<CreditCardApplicationResponse>
    getMyApplications() {

        Long userId =
                currentUser.getUserId();

        return applicationRepository
                .findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CreditCardApplicationResponse
    getMyApplication(Long applicationId) {

        CreditCardApplication application =
                getOwnedApplication(applicationId);

        return toResponse(application);
    }

    @Transactional
    public CreditCardApplicationResponse updateApplication(
            Long applicationId,
            UpdateCreditCardApplicationRequest request) {

        CreditCardApplication application =
                getOwnedApplication(applicationId);

        ensureDraft(application);

        application.setDateOfBirth(
                request.dateOfBirth()
        );
        application.setGender(
                request.gender()
        );
        application.setMaritalStatus(
                request.maritalStatus()
        );

        application.setAddressLine1(
                request.addressLine1().trim()
        );
        application.setAddressLine2(
                trimNullable(request.addressLine2())
        );
        application.setCity(
                request.city().trim()
        );
        application.setState(
                request.state().trim()
        );
        application.setPostalCode(
                request.postalCode().trim()
        );
        application.setCountry(
                request.country().trim()
        );

        application.setEmploymentType(
                request.employmentType()
        );
        application.setEmployerName(
                trimNullable(request.employerName())
        );
        application.setDesignation(
                trimNullable(request.designation())
        );
        application.setYearsOfExperience(
                request.yearsOfExperience()
        );

        application.setAnnualIncome(
                request.annualIncome()
        );
        application.setMonthlyExpenses(
                request.monthlyExpenses()
        );
        application.setExistingLoanAmount(
                defaultZero(request.existingLoanAmount())
        );
        application.setExistingEmiAmount(
                defaultZero(request.existingEmiAmount())
        );
        application.setOtherIncome(
                defaultZero(request.otherIncome())
        );

        validateRequestedLimit(
                request.requestedCreditLimit(),
                application.getProduct()
        );

        application.setRequestedCreditLimit(
                request.requestedCreditLimit()
        );

        application.setConsentGiven(
                request.consentGiven()
        );

        application.setDeclarationAccepted(
                request.declarationAccepted()
        );

        return toResponse(
                applicationRepository.save(application)
        );
    }

    @Transactional
    public CreditCardApplicationResponse submitApplication(
            Long applicationId) {

        CreditCardApplication application =
                getOwnedApplication(applicationId);

        ensureDraft(application);

        validateApplicationForSubmission(application);

        ApplicationStatus oldStatus =
                application.getStatus();

        application.setStatus(
                ApplicationStatus.SUBMITTED
        );

        application.setSubmittedAt(
                java.time.LocalDateTime.now()
        );

        application =
                applicationRepository.save(application);

        saveStatusHistory(
                application,
                oldStatus,
                ApplicationStatus.SUBMITTED,
                application.getUser(),
                "Application submitted by user"
        );

        return toResponse(application);
    }

    private CreditCardApplication getOwnedApplication(
            Long applicationId) {

        Long userId =
                currentUser.getUserId();

        return applicationRepository
                .findByIdAndUser_Id(
                        applicationId,
                        userId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Credit card application not found"
                        )
                );
    }

    private void ensureDraft(
            CreditCardApplication application) {

        if (application.getStatus()
                != ApplicationStatus.DRAFT) {

            throw new IllegalStateException(
                    "Only DRAFT applications can be modified"
            );
        }
    }

    private void validateApplicationForSubmission(
            CreditCardApplication application) {

        if (application.getDateOfBirth() == null) {
            throw new IllegalStateException(
                    "Date of birth is required"
            );
        }

        if (application.getGender() == null) {
            throw new IllegalStateException(
                    "Gender is required"
            );
        }

        if (application.getMaritalStatus() == null) {
            throw new IllegalStateException(
                    "Marital status is required"
            );
        }

        if (isBlank(application.getAddressLine1())
                || isBlank(application.getCity())
                || isBlank(application.getState())
                || isBlank(application.getPostalCode())
                || isBlank(application.getCountry())) {

            throw new IllegalStateException(
                    "Complete address details are required"
            );
        }

        if (application.getEmploymentType() == null) {
            throw new IllegalStateException(
                    "Employment type is required"
            );
        }

        if (application.getEmploymentType()
                == EmploymentType.SALARIED
                && isBlank(application.getEmployerName())) {

            throw new IllegalStateException(
                    "Employer name is required for salaried applicants"
            );
        }

        if (application.getAnnualIncome() == null
                || application.getAnnualIncome().signum() <= 0) {

            throw new IllegalStateException(
                    "Annual income must be greater than zero"
            );
        }

        if (application.getRequestedCreditLimit() == null
                || application.getRequestedCreditLimit().signum() <= 0) {

            throw new IllegalStateException(
                    "Requested credit limit must be greater than zero"
            );
        }

        if (!application.isConsentGiven()) {
            throw new IllegalStateException(
                    "Consent must be given"
            );
        }

        if (!application.isDeclarationAccepted()) {
            throw new IllegalStateException(
                    "Declaration must be accepted"
            );
        }
    }

    private void validateRequestedLimit(
            java.math.BigDecimal requestedLimit,
            CreditCardProduct product) {

        if (requestedLimit.compareTo(
                product.getMinCreditLimit()
        ) < 0) {

            throw new IllegalArgumentException(
                    "Requested credit limit is below the minimum allowed"
            );
        }

        if (requestedLimit.compareTo(
                product.getMaxCreditLimit()
        ) > 0) {

            throw new IllegalArgumentException(
                    "Requested credit limit exceeds the maximum allowed"
            );
        }
    }

    private void saveStatusHistory(
            CreditCardApplication application,
            ApplicationStatus oldStatus,
            ApplicationStatus newStatus,
            User changedBy,
            String remarks) {

        historyRepository.save(
                ApplicationStatusHistory.builder()
                        .application(application)
                        .oldStatus(oldStatus)
                        .newStatus(newStatus)
                        .changedBy(changedBy)
                        .remarks(remarks)
                        .build()
        );
    }

    private String buildApplicationNumber(Long id) {

        return "CC-"
                + java.time.Year.now().getValue()
                + "-"
                + String.format("%06d", id);
    }

    private String trimNullable(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }

    private java.math.BigDecimal defaultZero(
            java.math.BigDecimal value) {

        return value == null
                ? java.math.BigDecimal.ZERO
                : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private CreditCardApplicationResponse toResponse(
            CreditCardApplication application) {

        User user = application.getUser();
        CreditCardProduct product = application.getProduct();

        return new CreditCardApplicationResponse(
                application.getId(),
                application.getApplicationNumber(),

                product.getId(),
                product.getProductCode(),
                product.getName(),

                user.getName(),
                user.getEmail(),
                user.getMobile(),

                application.getStatus(),

                application.getDateOfBirth(),
                application.getGender(),
                application.getMaritalStatus(),

                application.getAddressLine1(),
                application.getAddressLine2(),
                application.getCity(),
                application.getState(),
                application.getPostalCode(),
                application.getCountry(),

                application.getEmploymentType(),
                application.getEmployerName(),
                application.getDesignation(),
                application.getYearsOfExperience(),

                application.getAnnualIncome(),
                application.getMonthlyExpenses(),
                application.getExistingLoanAmount(),
                application.getExistingEmiAmount(),
                application.getOtherIncome(),
                application.getRequestedCreditLimit(),

                application.isConsentGiven(),
                application.isDeclarationAccepted(),

                application.getSubmittedAt(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}