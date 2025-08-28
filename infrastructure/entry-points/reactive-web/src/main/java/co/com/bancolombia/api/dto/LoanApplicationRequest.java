package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new loan application")
public class LoanApplicationRequest {

    private static final String CLIENT_ID_DESCRIPTION = "Unique identifier of the client";
    private static final String CLIENT_ID_EXAMPLE = "1";
    private static final String CLIENT_ID_REQUIRED_MESSAGE = "Client ID is required";
    private static final String AMOUNT_DESCRIPTION = "Requested loan amount";
    private static final String AMOUNT_EXAMPLE = "50000.00";
    private static final String AMOUNT_MINIMUM = "0.01";
    private static final String AMOUNT_REQUIRED_MESSAGE = "Amount is required";
    private static final String AMOUNT_MIN_MESSAGE = "Amount must be greater than 0";
    private static final String TERM_DESCRIPTION = "Loan term in months";
    private static final String TERM_EXAMPLE = "12";
    private static final String TERM_MIN_VALUE = "1";
    private static final String TERM_REQUIRED_MESSAGE = "Term is required";
    private static final String TERM_MIN_MESSAGE = "Term must be at least 1 month";
    private static final String LOAN_TYPE_ID_DESCRIPTION = "ID of the loan type";
    private static final String LOAN_TYPE_ID_EXAMPLE = "1";
    private static final String LOAN_TYPE_ID_REQUIRED_MESSAGE = "Loan type ID is required";

    @Schema(description = CLIENT_ID_DESCRIPTION, example = CLIENT_ID_EXAMPLE)
    @NotBlank(message = CLIENT_ID_REQUIRED_MESSAGE)
    private String clientId;

    @Schema(description = AMOUNT_DESCRIPTION, example = AMOUNT_EXAMPLE, minimum = AMOUNT_MINIMUM)
    @NotNull(message = AMOUNT_REQUIRED_MESSAGE)
    @DecimalMin(value = AMOUNT_MINIMUM, message = AMOUNT_MIN_MESSAGE)
    private BigDecimal amount;

    @Schema(description = TERM_DESCRIPTION, example = TERM_EXAMPLE, minimum = TERM_MIN_VALUE)
    @NotNull(message = TERM_REQUIRED_MESSAGE)
    @Min(value = 1, message = TERM_MIN_MESSAGE)
    private Integer term;

    @Schema(description = LOAN_TYPE_ID_DESCRIPTION, example = LOAN_TYPE_ID_EXAMPLE)
    @NotNull(message = LOAN_TYPE_ID_REQUIRED_MESSAGE)
    private Long loanTypeId;
}