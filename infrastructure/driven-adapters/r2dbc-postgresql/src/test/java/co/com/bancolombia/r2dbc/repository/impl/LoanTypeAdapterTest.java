package co.com.bancolombia.r2dbc.adapter;

package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.r2dbc.repository.impl.LoanTypeRepositoryR2dbc;
import co.com.bancolombia.r2dbc.entity.LoanTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

class LoanTypeAdapterTest {

    @Mock
    private LoanTypeRepositoryR2dbc repository;

    @Mock
    private ObjectMapper mapper;

    private LoanTypeAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new LoanTypeAdapter(repository, mapper);
    }

    @Test
    void findById() {
        // Arrange
        Long id = 1L;
        LoanTypeEntity entity = LoanTypeEntity.builder()
                .id(id)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        LoanType loanType = LoanType.builder()
                .id(id)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(repository.findById(id)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, LoanType.class)).thenReturn(loanType);

        // Act
        Mono<LoanType> result = adapter.findById(id);

        // Assert
        StepVerifier.create(result)
                .expectNext(loanType)
                .verifyComplete();
    }

    @Test
    void existsById() {
        // Arrange
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = adapter.existsById(id);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByName() {
        // Arrange
        String name = "Personal Loan";
        LoanTypeEntity entity = LoanTypeEntity.builder()
                .id(1L)
                .name(name)
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        LoanType loanType = LoanType.builder()
                .id(1L)
                .name(name)
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(repository.findByName(name)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, LoanType.class)).thenReturn(loanType);

        // Act
        Mono<LoanType> result = adapter.findByName(name);

        // Assert
        StepVerifier.create(result)
                .expectNext(loanType)
                .verifyComplete();
    }
}