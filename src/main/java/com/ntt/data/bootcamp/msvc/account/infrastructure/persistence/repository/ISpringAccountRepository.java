package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.AccountCollection;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ISpringAccountRepository
    extends ISpringGenericRepository<AccountCollection, String> {
  Mono<Long> countByCustomerIdAndAccountType(String customerId, String accountType);

  Flux<AccountCollection> findByAccountType(String accountType);

  Mono<AccountCollection> findByAccountNumber(String accountNumber);

  Flux<AccountCollection> findByStatus(String status);

  Flux<AccountCollection> findByCustomerId(String customerId);

  @Query("{ 'accountType': ?0, 'status': ?1 }")
  Flux<AccountCollection> findByTypeAndStatus(String type, String status);
}
