package co.com.bancolombia.sqs.sender.services.debtCapacity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DebtCapacityResponseMessage {
    private UUID requestId;
    private UUID applicationId;
    private String decision;
    private BigDecimal maxCapacity;
    private BigDecimal availableCapacity;
    private BigDecimal monthlyPayment;
    private BigDecimal totalIncome;
    private BigDecimal currentDebt;
    private List<PaymentPlanItem> paymentPlan;
    private Instant responseDate;
    private String errorMessage;

    @Data
    @Builder
    public static class PaymentPlanItem {
        private Integer installmentNumber;
        private BigDecimal principalPayment;
        private BigDecimal interestPayment;
        private BigDecimal remainingBalance;
        private Instant dueDate;
    }
}