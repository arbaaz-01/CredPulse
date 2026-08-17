package com.ofss.project.entity;

import com.ofss.project.enums.ApplicationStatus;
import com.ofss.project.enums.Gender;
import com.ofss.project.enums.MaritalStatus;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "CREDIT_CARD_APPLICATIONS",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_APPLICATION_NUMBER",
                        columnNames = "APPLICATION_NUMBER"
                )
        },
        indexes = {
                @Index(
                        name = "IDX_APPLICATION_USER",
                        columnList = "USER_ID"
                ),
                @Index(
                        name = "IDX_APPLICATION_STATUS",
                        columnList = "STATUS"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCardApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "APPLICATION_NUMBER",
            unique = true,
            length = 40
    )
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "USER_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_APPLICATION_USER"
            )
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "PRODUCT_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_APPLICATION_PRODUCT"
            )
    )
    private CreditCardProduct product;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "STATUS",
            nullable = false,
            length = 30
    )
    private ApplicationStatus status;

    // Applicant details

    @Column(
            name = "DATE_OF_BIRTH"
    )
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "GENDER",
            length = 20
    )
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "MARITAL_STATUS",
            length = 20
    )
    private MaritalStatus maritalStatus;

    // Address

    @Column(
            name = "ADDRESS_LINE_1",
            length = 200
    )
    private String addressLine1;

    @Column(
            name = "ADDRESS_LINE_2",
            length = 200
    )
    private String addressLine2;

    @Column(
            name = "CITY",
            length = 100
    )
    private String city;

    @Column(
            name = "STATE",
            length = 100
    )
    private String state;

    @Column(
            name = "POSTAL_CODE",
            length = 20
    )
    private String postalCode;

    @Column(
            name = "COUNTRY",
            length = 100
    )
    private String country;

    // Employment

    @Enumerated(EnumType.STRING)
    @Column(
            name = "EMPLOYMENT_TYPE",
            length = 30
    )
    private com.ofss.project.enums.EmploymentType employmentType;

    @Column(
            name = "EMPLOYER_NAME",
            length = 150
    )
    private String employerName;

    @Column(
            name = "DESIGNATION",
            length = 100
    )
    private String designation;

    @Column(
            name = "YEARS_OF_EXPERIENCE",
            precision = 5,
            scale = 2
    )
    private BigDecimal yearsOfExperience;

    // Financial details

    @Column(
            name = "ANNUAL_INCOME",
            precision = 19,
            scale = 2
    )
    private BigDecimal annualIncome;

    @Column(
            name = "MONTHLY_EXPENSES",
            precision = 19,
            scale = 2
    )
    private BigDecimal monthlyExpenses;

    @Column(
            name = "EXISTING_LOAN_AMOUNT",
            precision = 19,
            scale = 2
    )
    private BigDecimal existingLoanAmount;

    @Column(
            name = "EXISTING_EMI_AMOUNT",
            precision = 19,
            scale = 2
    )
    private BigDecimal existingEmiAmount;

    @Column(
            name = "OTHER_INCOME",
            precision = 19,
            scale = 2
    )
    private BigDecimal otherIncome;

    @Column(
            name = "REQUESTED_CREDIT_LIMIT",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal requestedCreditLimit;

    // Declarations

    @Column(
            name = "CONSENT_GIVEN",
            nullable = false
    )
    private boolean consentGiven;

    @Column(
            name = "DECLARATION_ACCEPTED",
            nullable = false
    )
    private boolean declarationAccepted;

    @Column(name = "SUBMITTED_AT")
    private LocalDateTime submittedAt;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}