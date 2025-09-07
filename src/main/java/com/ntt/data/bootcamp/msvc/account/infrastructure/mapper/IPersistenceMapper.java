package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

public interface IPersistenceMapper<D, E> {
  E toEntity(D d);

  D toDomain(E e);
}
