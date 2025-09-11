package com.ntt.data.bootcamp.msvc.account.domain.port.out;

import com.ntt.data.bootcamp.msvc.account.domain.Account;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountRepositoryPort extends IGenericRepositoryPort<Account, String> {
  Mono<Long> countAccountsByCustomerIdAndAccountType(String customerId, String accountType);

  Flux<Account> findByAccountType(String accountType);

  Mono<Account> findByAccountNumber(String accountNumber);

  Flux<Account> findAll();

  Flux<Account> findByAccountType(AccountType type);

  Flux<Account> findByStatus(AccountStatus status);

  Flux<Account> findByCustomerId(String customerId);

  Flux<Account> findByTypeAndStatus(AccountType type, AccountStatus status);
}
