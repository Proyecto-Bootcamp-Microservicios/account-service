package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper.registry;

import com.ntt.data.bootcamp.msvc.account.domain.Account;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.AccountCollection;

public interface IAccountPersistenceMapperRegistry {
  Account toDomain(AccountCollection accountCollection);

  AccountCollection toEntity(Account account);
}
