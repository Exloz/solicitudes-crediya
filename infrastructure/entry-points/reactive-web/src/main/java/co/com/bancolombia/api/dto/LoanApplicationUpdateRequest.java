package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating loan application status")
public class LoanApplicationUpdateRequest {

    private static final String STATUS_ID_DESCRIPTION = "New status ID for the loan application (3=Approved, 4=Rejected)";
    private static final String STATUS_ID_EXAMPLE = "3";
    private static final String STATUS_ID_MIN_VALUE = "3";
    private static final String STATUS_ID_MAX_VALUE = "4";
    private static final String STATUS_ID_REQUIRED_MESSAGE = "Status ID is required";
    private static final String STATUS_ID_MIN_MESSAGE = "Status ID must be 3 (Approved) or 4 (Rejected)";
    private static final String STATUS_ID_MAX_MESSAGE = "Status ID must be 3 (Approved) or 4 (Rejected)";

    @Schema(description = STATUS_ID_DESCRIPTION, example = STATUS_ID_EXAMPLE, minimum = STATUS_ID_MIN_VALUE, maximum = STATUS_ID_MAX_VALUE)
    @NotNull(message = STATUS_ID_REQUIRED_MESSAGE)
    @Min(value = 3, message = STATUS_ID_MIN_MESSAGE)
    @Max(value = 4, message = STATUS_ID_MAX_MESSAGE)
    private Long statusId;
}