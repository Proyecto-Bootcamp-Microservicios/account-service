package com.ntt.data.bootcamp.msvc.account.infrastructure.adapter.out;

import com.ntt.data.bootcamp.msvc.account.domain.Account;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.infrastructure.mapper.registry.IAccountPersistenceMapperRegistry;
import com.ntt.data.bootcamp.msvc.account.infrastructure.mapper.registry.IPersistenceMapperRegistry;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.AccountCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringAccountRepository;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringGenericRepository;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.impl.AbstractSpringRepositoryImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public class AccountRepositoryAdapter extends AbstractSpringRepositoryImpl<Account, AccountCollection, String> implements IAccountRepositoryPort {

  private final ISpringAccountRepository repository;
  private final IAccountPersistenceMapperRegistry accountPersistenceMapperRegistry;

  protected AccountRepositoryAdapter(ISpringAccountRepository repository, IAccountPersistenceMapperRegistry accountPersistenceMapperRegistry) {
    super(repository, accountPersistenceMapperRegistry);
    this.repository = repository;
    this.accountPersistenceMapperRegistry = accountPersistenceMapperRegistry;
  }

  @Override
  public Mono<Long> countAccountsByCustomerIdAndAccountType(String customerId, String accountType) {
    return repository.countByCustomerIdAndAccountType(customerId, accountType);
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

  @Override
  public Flux<Account> findByAccountType(AccountType type) {
    return repository
        .findByAccountType(type.name())
        .map(accountPersistenceMapperRegistry::toDomain);
  }

  @Override
  public Flux<Account> findByStatus(AccountStatus status) {
    return repository
        .findByStatus(status.name())
        .map(accountPersistenceMapperRegistry::toDomain);
  }

  @Override
  public Flux<Account> findByCustomerId(String customerId) {
    return repository
        .findByCustomerId(customerId)
        .map(accountPersistenceMapperRegistry::toDomain);
  }

  @Override
  public Flux<Account> findByTypeAndStatus(AccountType type, AccountStatus status) {
    return repository
        .findByTypeAndStatus(type.name(), status.name())
        .map(accountPersistenceMapperRegistry::toDomain);
  }
}
