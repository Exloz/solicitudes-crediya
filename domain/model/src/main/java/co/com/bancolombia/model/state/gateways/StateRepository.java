package co.com.bancolombia.model.state.gateways;

import co.com.bancolombia.model.state.State;
import reactor.core.publisher.Mono;

public interface StateRepository {

    Mono<State> findById(Long id);

    Mono<State> findByName(String name);
}
