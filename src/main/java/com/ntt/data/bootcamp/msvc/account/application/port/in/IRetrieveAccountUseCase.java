package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface IRetrieveAccountUseCase {
  Mono<AccountResponse> findById(String id);                          // por id
  Flux<AccountResponse> findAll();                                   // sin filtros
  Flux<AccountResponse> findAllByType(AccountType type);             // por tipo
  Flux<AccountResponse> findAllByStatus(AccountStatus status);       // por estado
  Flux<AccountResponse> findAllByCustomer(String customerId);        // por cliente
  Flux<AccountResponse> findAllByTypeAndStatus(AccountType type, AccountStatus status); // combinados
}
