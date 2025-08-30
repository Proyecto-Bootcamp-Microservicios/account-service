package com.NTTDATA.bootcamp.msvc_account.infrastructure.adapter.out;

import com.NTTDATA.bootcamp.msvc_account.domain.CheckingAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.ICheckingAccountRepositoryPort;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper.ICheckingAccountPersistenceMapper;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.CheckingAccountCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.repository.ISpringCheckingAccountRepository;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.repository.impl.AbstractSpringRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CheckingAccountRepositoryAdapter extends AbstractSpringRepositoryImpl<CheckingAccount, CheckingAccountCollection, String> implements ICheckingAccountRepositoryPort {

    protected CheckingAccountRepositoryAdapter(ISpringCheckingAccountRepository repository, ICheckingAccountPersistenceMapper mapper) {
        super(repository, mapper);
    }
}
