package com.ntt.data.bootcamp.msvc.account.domain.port.out;

import com.ntt.data.bootcamp.msvc.account.domain.Account;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound repository port for persisting and querying {@link Account} aggregates.
 */
public interface IAccountRepositoryPort extends IGenericRepositoryPort<Account, String> {
  /** Counts accounts for a customer by account type. */
  Mono<Long> countAccountsByCustomerIdAndAccountType(String customerId, String accountType);

  /** Finds accounts by account type (string representation). */
  Flux<Account> findByAccountType(String accountType);

  /** Finds an account by its internal account number. */
  Mono<Account> findByAccountNumber(String accountNumber);

  /** Returns all accounts. */
  Flux<Account> findAll();

  /** Finds accounts by typed account type. */
  Flux<Account> findByAccountType(AccountType type);

  /** Finds accounts by status. */
  Flux<Account> findByStatus(AccountStatus status);

  /** Finds accounts by customer id. */
  Flux<Account> findByCustomerId(String customerId);

  /** Finds accounts filtered by type and status. */
  Flux<Account> findByTypeAndStatus(AccountType type, AccountStatus status);
}
