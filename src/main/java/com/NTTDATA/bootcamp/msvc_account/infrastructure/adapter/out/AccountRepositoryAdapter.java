package com.NTTDATA.bootcamp.msvc_account.infrastructure.adapter.out;

import com.NTTDATA.bootcamp.msvc_account.domain.Account;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.IAccountRepositoryPort;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper.registry.IAccountPersistenceMapperRegistry;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.repository.ISpringAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class AccountRepositoryAdapter implements IAccountRepositoryPort {

    private final ISpringAccountRepository repository;
    private final IAccountPersistenceMapperRegistry accountPersistenceMapperRegistry;

    @Override
    public Mono<Long> countAccountsByCustomerIdAndAccountType(String customerId, String accountType) {
        return repository.countByCustomerIdAndAccountType(customerId, accountType);
    }

    @Override
    public Mono<Account> findById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.empty())
                .map(accountPersistenceMapperRegistry::toDomain);
    }

    @Override
    public Mono<Account> save(Account account) {
        return repository.save(accountPersistenceMapperRegistry.toEntity(account))
                .map(accountPersistenceMapperRegistry::toDomain);
    }

    @Override
    public Flux<Account> findByAccountType(String accountType) {
        return repository.findByAccountType(accountType)
                .map(accountPersistenceMapperRegistry::toDomain);
    }
}
