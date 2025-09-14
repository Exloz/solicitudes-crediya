package co.com.bancolombia.sqs.sender.services.debtCapacity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DebtCapacityReqMessage {
    private UUID requestId;
    private UUID applicationId;
    private String clientId;
    private String clientEmail;
    private BigDecimal baseSalary;
    private BigDecimal loanAmount;
    private Integer loanTerm;
    private Long loanTypeId;
    private BigDecimal interestRate;
    private Instant requestDate;
}
