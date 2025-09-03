package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_application")
public class LoanApplicationEntity {
    @Id
    private UUID id;

    @Column("client_id")
    private String clientId;

    @Column("amount")
    private BigDecimal amount;

    @Column("term")
    private Integer term;

    @Column("loan_type_id")
    private Long loanTypeId;

    @Column("status")
    private Long status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}