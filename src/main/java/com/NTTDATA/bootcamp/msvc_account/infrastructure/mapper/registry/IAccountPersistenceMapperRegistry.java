package com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper.registry;

import com.NTTDATA.bootcamp.msvc_account.domain.Account;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.AccountCollection;

public interface IAccountPersistenceMapperRegistry {
    Account toDomain(AccountCollection accountCollection);
    AccountCollection toEntity(Account account);
}
