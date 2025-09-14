package co.com.bancolombia.sqs.sender.services.loanApplication;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@lombok.Data
@lombok.Builder
public class NotificationMessage {
    private UUID applicationId;
    private String clientId;
    private String clientEmail;
    private Long newStatus;
    private Instant decisionTimestamp;
    private BigDecimal amount;
    private Integer term;
    private Long loanTypeId;

}
