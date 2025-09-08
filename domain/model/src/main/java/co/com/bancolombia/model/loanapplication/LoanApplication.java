package co.com.bancolombia.model.loanapplication;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private UUID id;
    private String clientId;
    private BigDecimal amount;
    private Integer term;
    private Long loanTypeId;
    private Long statusId;
    private LocalDateTime createdAt;
}
