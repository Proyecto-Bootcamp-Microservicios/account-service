package com.ntt.data.bootcamp.msvc.account.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Generic reactive repository port abstraction.
 *
 * @param <D> domain aggregate type
 * @param <ID> identifier type
 */
public interface IGenericRepositoryPort<D, ID> {
  /** Persists a domain aggregate. */
  Mono<D> save(D domain);

  /** Retrieves a domain aggregate by id. */
  Mono<D> findById(ID id);

  /** Retrieves all domain aggregates. */
  Flux<D> findAll();
}
