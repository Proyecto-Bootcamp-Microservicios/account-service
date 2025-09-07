package com.ntt.data.bootcamp.msvc.account.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGenericRepositoryPort<D, ID> {
  Mono<D> save(D domain);

  Mono<D> findById(ID id);

  Mono<Void> delete(ID id);

  Flux<D> findAll();
}
