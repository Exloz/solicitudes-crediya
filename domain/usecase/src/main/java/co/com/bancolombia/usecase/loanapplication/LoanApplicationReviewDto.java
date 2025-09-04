package co.com.bancolombia.usecase.loanapplication;

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
public class LoanApplicationReviewDto {
    private UUID id;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private String fullName;
    private String loanType;
    private BigDecimal interestRate;
    private String applicationStatus;
    private BigDecimal baseSalary;
    private BigDecimal totalMonthlyDebtFromApprovedApplications;
    private LocalDateTime createdAt;
}