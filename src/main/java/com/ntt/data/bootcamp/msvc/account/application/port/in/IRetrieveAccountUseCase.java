package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use case for retrieving accounts with various filters.
 */
public interface IRetrieveAccountUseCase {
  /** Retrieves an account by its internal identifier. */
  Mono<AccountResponse> findById(String id);

  /** Retrieves all accounts without filters. */
  Flux<AccountResponse> findAll();

  /** Retrieves all accounts of a given type. */
  Flux<AccountResponse> findAllByType(AccountType type);

  /** Retrieves all accounts with the given status. */
  Flux<AccountResponse> findAllByStatus(AccountStatus status);

  /** Retrieves all accounts belonging to a customer. */
  Flux<AccountResponse> findAllByCustomer(String customerId);

  /** Retrieves all accounts filtered by type and status. */
  Flux<AccountResponse> findAllByTypeAndStatus(AccountType type, AccountStatus status);
}
