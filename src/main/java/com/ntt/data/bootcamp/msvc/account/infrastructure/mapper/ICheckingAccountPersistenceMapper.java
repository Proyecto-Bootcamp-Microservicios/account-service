package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

import com.ntt.data.bootcamp.msvc.account.domain.CheckingAccount;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.CheckingAccountCollection;

public interface ICheckingAccountPersistenceMapper
    extends IPersistenceMapper<CheckingAccount, CheckingAccountCollection> {}
