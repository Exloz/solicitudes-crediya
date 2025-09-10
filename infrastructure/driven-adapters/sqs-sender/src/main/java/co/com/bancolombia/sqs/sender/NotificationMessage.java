package co.com.bancolombia.sqs.sender;

import java.time.LocalDateTime;
import java.util.UUID;

@lombok.Data
@lombok.Builder
public class NotificationMessage {
    private UUID applicationId;
    private String clientId;
    private String clientEmail;
    private Long newStatus;
    private LocalDateTime decisionTimestamp;
    private java.math.BigDecimal amount;
    private Integer term;
    private Long loanTypeId;

}
