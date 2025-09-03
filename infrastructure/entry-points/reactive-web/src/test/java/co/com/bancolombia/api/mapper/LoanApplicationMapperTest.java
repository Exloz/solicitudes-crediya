package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.dto.LoanApplicationResponse;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationMapperTest {

    private final LoanApplicationMapper mapper = Mappers.getMapper(LoanApplicationMapper.class);

    @Test
    void toModel() {
        // Arrange
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        // Act
        LoanApplication result = mapper.toModel(request);

        // Assert
        assertNull(result.getId()); // ID should be ignored in mapping
        assertEquals("client123", result.getClientId());
        assertEquals(new BigDecimal("50000"), result.getAmount());
        assertEquals(12, result.getTerm());
        assertEquals(1L, result.getLoanTypeId());
        assertNull(result.getStatus());
        assertNull(result.getCreatedAt());
    }

    @Test
    void toResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        LoanApplication loanApplication = LoanApplication.builder()
                .id(id)
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .status(1L)
                .createdAt(createdAt)
                .build();

        // Act
        LoanApplicationResponse result = mapper.toResponse(loanApplication);

        // Assert
        assertEquals(id, result.getId());
        assertEquals("client123", result.getClientId());
        assertEquals(new BigDecimal("50000"), result.getAmount());
        assertEquals(12, result.getTerm());
        assertEquals(1L, result.getLoanTypeId());
        assertEquals(1L, result.getStatus());
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void toModel_withNullRequest() {
        // Act
        LoanApplication result = mapper.toModel(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toResponse_withNullLoanApplication() {
        // Act
        LoanApplicationResponse result = mapper.toResponse(null);

        // Assert
        assertNull(result);
    }
}