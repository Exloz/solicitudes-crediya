package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing loan application details for review")
public class LoanApplicationReviewResponse {

    private static final String ID_DESCRIPTION = "Unique identifier of the loan application";
    private static final String ID_EXAMPLE = "123e4567-e89b-12d3-a456-426614174000";
    private static final String AMOUNT_DESCRIPTION = "Requested loan amount";
    private static final String AMOUNT_EXAMPLE = "50000.00";
    private static final String TERM_DESCRIPTION = "Loan term in months";
    private static final String TERM_EXAMPLE = "12";
    private static final String EMAIL_DESCRIPTION = "Client's email address";
    private static final String EMAIL_EXAMPLE = "john.doe@example.com";
    private static final String FULL_NAME_DESCRIPTION = "Client's full name";
    private static final String FULL_NAME_EXAMPLE = "John Doe";
    private static final String LOAN_TYPE_DESCRIPTION = "Type of loan requested";
    private static final String LOAN_TYPE_EXAMPLE = "Personal Loan";
    private static final String INTEREST_RATE_DESCRIPTION = "Interest rate for the loan type";
    private static final String INTEREST_RATE_EXAMPLE = "0.15";
    private static final String APPLICATION_STATUS_DESCRIPTION = "Current status of the loan application";
    private static final String APPLICATION_STATUS_EXAMPLE = "Pending review";
    private static final String BASE_SALARY_DESCRIPTION = "Client's base salary";
    private static final String BASE_SALARY_EXAMPLE = "3000.00";
    private static final String TOTAL_MONTHLY_DEBT_DESCRIPTION = "Total monthly request amount";
    private static final String TOTAL_MONTHLY_DEBT_EXAMPLE = "500.00";
    private static final String CREATED_AT_DESCRIPTION = "Timestamp when the application was created";
    private static final String CREATED_AT_EXAMPLE = "2024-01-15T10:30:00";

    @Schema(description = ID_DESCRIPTION, example = ID_EXAMPLE)
    private UUID id;

    @Schema(description = AMOUNT_DESCRIPTION, example = AMOUNT_EXAMPLE)
    private BigDecimal amount;

    @Schema(description = TERM_DESCRIPTION, example = TERM_EXAMPLE)
    private Integer term;

    @Schema(description = EMAIL_DESCRIPTION, example = EMAIL_EXAMPLE)
    private String email;

    @Schema(description = FULL_NAME_DESCRIPTION, example = FULL_NAME_EXAMPLE)
    private String fullName;

    @Schema(description = LOAN_TYPE_DESCRIPTION, example = LOAN_TYPE_EXAMPLE)
    private String loanType;

    @Schema(description = INTEREST_RATE_DESCRIPTION, example = INTEREST_RATE_EXAMPLE)
    private BigDecimal interestRate;

    @Schema(description = APPLICATION_STATUS_DESCRIPTION, example = APPLICATION_STATUS_EXAMPLE)
    private String applicationStatus;

    @Schema(description = BASE_SALARY_DESCRIPTION, example = BASE_SALARY_EXAMPLE)
    private BigDecimal baseSalary;

    @Schema(description = TOTAL_MONTHLY_DEBT_DESCRIPTION, example = TOTAL_MONTHLY_DEBT_EXAMPLE)
    private BigDecimal monthlyRequestAmount;

    @Schema(description = CREATED_AT_DESCRIPTION, example = CREATED_AT_EXAMPLE)
    private LocalDateTime createdAt;
}