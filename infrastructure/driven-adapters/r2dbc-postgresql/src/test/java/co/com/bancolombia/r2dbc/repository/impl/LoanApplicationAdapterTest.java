package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanApplicationAdapterTest {

    @Mock
    private LoanApplicationRepositoryR2dbc repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    private LoanApplicationAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
        adapter = new LoanApplicationAdapter(repository, mapper, transactionalOperator);
    }

    @Test
    void saveLoanApplication() {
        // Arrange
        UUID id = UUID.randomUUID();
        LoanApplication loanApplication = LoanApplication.builder()
                .id(id)
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        LoanApplicationEntity entity = LoanApplicationEntity.builder()
                .id(id)
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(mapper.map(loanApplication, LoanApplicationEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, LoanApplication.class)).thenReturn(loanApplication);

        // Act
        Mono<LoanApplication> result = adapter.saveLoanApplication(loanApplication);

        // Assert
        StepVerifier.create(result)
                .expectNext(loanApplication)
                .verifyComplete();
    }

    @Test
    void findById() {
        // Arrange
        UUID id = UUID.randomUUID();
        LoanApplicationEntity entity = LoanApplicationEntity.builder()
                .id(id)
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        LoanApplication loanApplication = LoanApplication.builder()
                .id(id)
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(id)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, LoanApplication.class)).thenReturn(loanApplication);

        // Act
        Mono<LoanApplication> result = adapter.findById(id);

        // Assert
        StepVerifier.create(result)
                .expectNext(loanApplication)
                .verifyComplete();
    }

    @Test
    void findByClientId() {
        // Arrange
        String clientId = "client123";
        LoanApplicationEntity entity1 = LoanApplicationEntity.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        LoanApplicationEntity entity2 = LoanApplicationEntity.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .amount(new BigDecimal("30000"))
                .term(6)
                .loanTypeId(2L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        LoanApplication loanApplication1 = LoanApplication.builder()
                .id(entity1.getId())
                .clientId(clientId)
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        LoanApplication loanApplication2 = LoanApplication.builder()
                .id(entity2.getId())
                .clientId(clientId)
                .amount(new BigDecimal("30000"))
                .term(6)
                .loanTypeId(2L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findByClientId(clientId)).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, LoanApplication.class)).thenReturn(loanApplication1);
        when(mapper.map(entity2, LoanApplication.class)).thenReturn(loanApplication2);

        // Act
        Flux<LoanApplication> result = adapter.findByClientId(clientId);

        // Assert
        StepVerifier.create(result)
                .expectNext(loanApplication1)
                .expectNext(loanApplication2)
                .verifyComplete();
    }
}