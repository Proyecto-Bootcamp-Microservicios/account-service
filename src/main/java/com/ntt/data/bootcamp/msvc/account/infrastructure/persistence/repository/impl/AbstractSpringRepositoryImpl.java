package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.impl;

import com.ntt.data.bootcamp.msvc.account.domain.port.out.IGenericRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.infrastructure.mapper.registry.IPersistenceMapperRegistry;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringGenericRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Base adapter bridging domain repository port with Spring Data reactive repositories.
 * Handles mapping between domain aggregates and persistence entities.
 */
public abstract class AbstractSpringRepositoryImpl<D, E, ID>
    implements IGenericRepositoryPort<D, ID> {

  private final ISpringGenericRepository<E, ID> repository;
  private final IPersistenceMapperRegistry<D, E> persistenceMapperRegistry;

  /** Constructs the adapter with a Spring repository and a mapper registry. */
  protected AbstractSpringRepositoryImpl(
      ISpringGenericRepository<E, ID> repository, IPersistenceMapperRegistry<D, E> persistenceMapperRegistry) {
    this.repository = repository;
    this.persistenceMapperRegistry = persistenceMapperRegistry;
  }

  @Override
  public Mono<D> findById(ID id) {
    return repository.findById(id).map(persistenceMapperRegistry::toDomain);
  }

  @Override
  public Flux<D> findAll() {
    return repository.findAll().map(persistenceMapperRegistry::toDomain);
  }

  @Override
  public Mono<D> save(D d) {
    return repository.save(persistenceMapperRegistry.toEntity(d)).map(persistenceMapperRegistry::toDomain);
  }
}
