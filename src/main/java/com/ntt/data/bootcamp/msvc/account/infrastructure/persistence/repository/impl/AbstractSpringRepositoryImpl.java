package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.impl;

import com.ntt.data.bootcamp.msvc.account.domain.port.out.IGenericRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.infrastructure.mapper.registry.IPersistenceMapperRegistry;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringGenericRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractSpringRepositoryImpl<D, E, ID>
    implements IGenericRepositoryPort<D, ID> {

  private final ISpringGenericRepository<E, ID> repository;
  private final IPersistenceMapperRegistry<D, E> persistenceMapperRegistry;

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
