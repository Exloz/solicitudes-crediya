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

    @Schema(description = "Unique identifier of the loan application", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Unique identifier of the client", example = "1")
    private String clientId;

    @Schema(description = "Approved loan amount", example = "50000.00")
    private BigDecimal amount;

    @Schema(description = "Loan term in months", example = "12")
    private Integer term;

    @Schema(description = "ID of the loan type", example = "1")
    private Long loanTypeId;

    @Schema(description = "ID of the application status", example = "1")
    private Long status;

    @Schema(description = "Timestamp when the application was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}