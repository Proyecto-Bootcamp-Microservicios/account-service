package com.ntt.data.bootcamp.msvc.account.domain.port.out;

import com.ntt.data.bootcamp.msvc.account.domain.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountRepositoryPort {
  Mono<Long> countAccountsByCustomerIdAndAccountType(String customerId, String accountType);

  Mono<Account> findById(String id);

  Mono<Account> save(Account account);

  Flux<Account> findByAccountType(String accountType);

  Mono<Account> findByAccountNumber(String accountNumber);
}
