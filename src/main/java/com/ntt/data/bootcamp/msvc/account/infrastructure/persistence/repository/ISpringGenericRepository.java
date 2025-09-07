package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ISpringGenericRepository<E, ID> extends ReactiveMongoRepository<E, ID> {}
