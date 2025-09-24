package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

import com.ntt.data.bootcamp.msvc.account.domain.PymeCheckingAccount;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.PymeCheckingAccountCollection;

public interface IPymeCheckingAccountPersistenceMapper
  extends IPersistenceMapper<PymeCheckingAccount, PymeCheckingAccountCollection> {
}
