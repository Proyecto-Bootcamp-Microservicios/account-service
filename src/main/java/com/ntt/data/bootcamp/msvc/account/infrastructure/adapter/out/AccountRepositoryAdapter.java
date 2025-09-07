package com.ntt.data.bootcamp.msvc.account.infrastructure.adapter.out;

import com.ntt.data.bootcamp.msvc.account.domain.Account;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.infrastructure.mapper.registry.IAccountPersistenceMapperRegistry;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringAccountRepository;
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
    return repository
        .findById(id)
        .switchIfEmpty(Mono.empty())
        .map(accountPersistenceMapperRegistry::toDomain);
  }

  @Override
  public Mono<Account> save(Account account) {
    return repository
        .save(accountPersistenceMapperRegistry.toEntity(account))
        .map(accountPersistenceMapperRegistry::toDomain);
  }

  @Override
  public Flux<Account> findByAccountType(String accountType) {
    return repository
        .findByAccountType(accountType)
        .map(accountPersistenceMapperRegistry::toDomain);
  }

  @Override
  public Mono<Account> findByAccountNumber(String accountNumber) {
    return repository
        .findByAccountNumber(accountNumber)
        .switchIfEmpty(Mono.empty())
        .map(accountPersistenceMapperRegistry::toDomain);
  }
}
