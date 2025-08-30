package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.repository.impl;

import com.NTTDATA.bootcamp.msvc_account.domain.port.out.IGenericRepositoryPort;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper.IPersistenceMapper;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.repository.ISpringGenericRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractSpringRepositoryImpl<D, E, ID> implements IGenericRepositoryPort<D, ID> {

    private final ISpringGenericRepository<E, ID> repository;
    private final IPersistenceMapper<D, E> persistenceMapper;

    protected AbstractSpringRepositoryImpl(ISpringGenericRepository<E, ID> repository, IPersistenceMapper<D, E> persistenceMapper) {
        this.repository = repository;
        this.persistenceMapper = persistenceMapper;
    }

    @Override
    public Mono<Void> delete(ID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<D> findById(ID id) {
        return repository.findById(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Flux<D> findAll() {
        return repository.findAll()
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Mono<D> save(D d) {
        return repository.save(persistenceMapper.toEntity(d))
                .map(persistenceMapper::toDomain);
    }
}
