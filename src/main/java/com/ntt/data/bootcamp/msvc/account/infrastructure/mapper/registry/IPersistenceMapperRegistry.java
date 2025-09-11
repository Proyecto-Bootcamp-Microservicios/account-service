package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper.registry;

public interface IPersistenceMapperRegistry<D, E> {
  E toEntity(D d);

  D toDomain(E e);
}
