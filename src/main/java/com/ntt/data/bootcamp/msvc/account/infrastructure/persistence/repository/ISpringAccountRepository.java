package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.AccountCollection;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reactive Spring Data repository for account collections.
 */
public interface ISpringAccountRepository
    extends ISpringGenericRepository<AccountCollection, String> {
  /** Counts accounts by customer and type. */
  Mono<Long> countByCustomerIdAndAccountType(String customerId, String accountType);

  /** Finds accounts by type (string). */
  Flux<AccountCollection> findByAccountType(String accountType);

  /** Finds an account by its internal number. */
  Mono<AccountCollection> findByAccountNumber(String accountNumber);

  /** Finds accounts by status. */
  Flux<AccountCollection> findByStatus(String status);

  /** Finds accounts by customer id. */
  Flux<AccountCollection> findByCustomerId(String customerId);

  /** Finds accounts by type and status. */
  @Query("{ 'accountType': ?0, 'status': ?1 }")
  Flux<AccountCollection> findByTypeAndStatus(String type, String status);

  Flux<AccountCollection> findByAccountTypeAndCustomerId(String accountType, String customerId);
}
