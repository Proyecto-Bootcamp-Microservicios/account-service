package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

import com.ntt.data.bootcamp.msvc.account.domain.SavingAccount;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.SavingAccountCollection;

public interface ISavingAccountPersistenceMapper
    extends IPersistenceMapper<SavingAccount, SavingAccountCollection> {}
