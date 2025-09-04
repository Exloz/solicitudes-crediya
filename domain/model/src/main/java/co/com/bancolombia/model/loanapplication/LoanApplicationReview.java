package co.com.bancolombia.model.loanapplication;

import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationReview {
    private UUID id;
    private BigDecimal amount;
    private Integer term;
    private LoanApplication loanApplication;
    private LoanType loanType;
    private State state;
    private UserInfo userInfo;
    private BigDecimal totalMonthlyDebt;
    private LocalDateTime createdAt;
}