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
@Schema(description = "Response object containing loan application details")
public class LoanApplicationResponse {

    private static final String ID_DESCRIPTION = "Unique identifier of the loan application";
    private static final String ID_EXAMPLE = "123e4567-e89b-12d3-a456-426614174000";
    private static final String CLIENT_ID_DESCRIPTION = "Unique identifier of the client";
    private static final String CLIENT_ID_EXAMPLE = "1";
    private static final String AMOUNT_DESCRIPTION = "Approved loan amount";
    private static final String AMOUNT_EXAMPLE = "50000.00";
    private static final String TERM_DESCRIPTION = "Loan term in months";
    private static final String TERM_EXAMPLE = "12";
    private static final String LOAN_TYPE_ID_DESCRIPTION = "ID of the loan type";
    private static final String LOAN_TYPE_ID_EXAMPLE = "1";
    private static final String STATUS_DESCRIPTION = "ID of the application status";
    private static final String STATUS_EXAMPLE = "1";
    private static final String CREATED_AT_DESCRIPTION = "Timestamp when the application was created";
    private static final String CREATED_AT_EXAMPLE = "2024-01-15T10:30:00";

    @Schema(description = ID_DESCRIPTION, example = ID_EXAMPLE)
    private UUID id;

    @Schema(description = CLIENT_ID_DESCRIPTION, example = CLIENT_ID_EXAMPLE)
    private String clientId;

    @Schema(description = AMOUNT_DESCRIPTION, example = AMOUNT_EXAMPLE)
    private BigDecimal amount;

    @Schema(description = TERM_DESCRIPTION, example = TERM_EXAMPLE)
    private Integer term;

    @Schema(description = LOAN_TYPE_ID_DESCRIPTION, example = LOAN_TYPE_ID_EXAMPLE)
    private Long loanTypeId;

    @Schema(description = STATUS_DESCRIPTION, example = STATUS_EXAMPLE)
    private Long status;

    @Schema(description = CREATED_AT_DESCRIPTION, example = CREATED_AT_EXAMPLE)
    private LocalDateTime createdAt;
}