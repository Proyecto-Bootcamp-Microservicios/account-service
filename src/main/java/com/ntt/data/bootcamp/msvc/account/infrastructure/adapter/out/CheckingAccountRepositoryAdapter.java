package com.ntt.data.bootcamp.msvc.account.infrastructure.adapter.out;

import com.ntt.data.bootcamp.msvc.account.domain.CheckingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.ICheckingAccountRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.infrastructure.mapper.IPersistenceMapper;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.CheckingAccountCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringCheckingAccountRepository;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.impl.AbstractSpringRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CheckingAccountRepositoryAdapter
        extends AbstractSpringRepositoryImpl<CheckingAccount, CheckingAccountCollection, String>
        implements ICheckingAccountRepositoryPort {

    protected CheckingAccountRepositoryAdapter(
            ISpringCheckingAccountRepository repository,
            IPersistenceMapper<CheckingAccount, CheckingAccountCollection> mapper) {
        super(repository, mapper);
    }
}
