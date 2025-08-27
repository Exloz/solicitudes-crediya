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

    @Schema(description = "Unique identifier of the client", example = "1")
    @NotBlank(message = "Client ID is required")
    private String clientId;

    @Schema(description = "Requested loan amount", example = "50000.00", minimum = "0.01")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Schema(description = "Loan term in months", example = "12", minimum = "1")
    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer term;

    @Schema(description = "ID of the loan type", example = "1")
    @NotNull(message = "Loan type ID is required")
    private Long loanTypeId;
}