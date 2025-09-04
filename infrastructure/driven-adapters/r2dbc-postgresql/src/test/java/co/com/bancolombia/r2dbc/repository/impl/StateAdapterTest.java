package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.model.state.State;
import co.com.bancolombia.r2dbc.repository.impl.StateRepositoryR2dbc;
import co.com.bancolombia.r2dbc.entity.StateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class StateAdapterTest {

    @Mock
    private StateRepositoryR2dbc repository;

    @Mock
    private ObjectMapper mapper;

    private StateAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new StateAdapter(repository, mapper);
    }

    @Test
    void findById() {
        // Arrange
        Long id = 1L;
        StateEntity entity = StateEntity.builder()
                .id(id)
                .name("Pending review")
                .description("Application is pending review")
                .build();

        State state = State.builder()
                .id(id)
                .name("Pending review")
                .description("Application is pending review")
                .build();

        when(repository.findById(id)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, State.class)).thenReturn(state);

        // Act
        Mono<State> result = adapter.findById(id);

        // Assert
        StepVerifier.create(result)
                .expectNext(state)
                .verifyComplete();
    }

    @Test
    void findByName() {
        // Arrange
        String name = "Pending review";
        StateEntity entity = StateEntity.builder()
                .id(1L)
                .name(name)
                .description("Application is pending review")
                .build();

        State state = State.builder()
                .id(1L)
                .name(name)
                .description("Application is pending review")
                .build();

        when(repository.findByName(name)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, State.class)).thenReturn(state);

        // Act
        Mono<State> result = adapter.findByName(name);

        // Assert
        StepVerifier.create(result)
                .expectNext(state)
                .verifyComplete();
    }
}