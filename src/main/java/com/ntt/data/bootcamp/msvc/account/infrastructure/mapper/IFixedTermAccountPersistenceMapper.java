package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

import com.ntt.data.bootcamp.msvc.account.domain.FixedTermAccount;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.FixedTermAccountCollection;

public interface IFixedTermAccountPersistenceMapper
    extends IPersistenceMapper<FixedTermAccount, FixedTermAccountCollection> {}
