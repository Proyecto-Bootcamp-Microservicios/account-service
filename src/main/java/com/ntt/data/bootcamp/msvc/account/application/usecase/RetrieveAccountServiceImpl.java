package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.IRetrieveAccountUseCase;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service implementing the account retrieval use cases.
 */
@AllArgsConstructor
public class RetrieveAccountServiceImpl implements IRetrieveAccountUseCase {

  private final IAccountRepositoryPort accountRepository;

  @Override
  public Mono<AccountResponse> findById(String id) {
    return accountRepository.findById(id)
        .map(account -> new AccountResponse(account.getIdValue(), account.getAccountNumber(), account.getExternalAccountNumber(), account.getAccountType(), account.getStatus(), account.getBalance().getAmount(), account.getBalance().getCurrency().getCurrencyCode(), account.getCreatedAt(), account.getUpdatedAt()));
  }

  @Override
  public Flux<AccountResponse> findAll() {
    return accountRepository.findAll()
        .map(account -> new AccountResponse(account.getIdValue(), account.getAccountNumber(), account.getExternalAccountNumber(), account.getAccountType(), account.getStatus(), account.getBalance().getAmount(), account.getBalance().getCurrency().getCurrencyCode(), account.getCreatedAt(), account.getUpdatedAt()));
  }

  @Override
  public Flux<AccountResponse> findAllByType(AccountType type) {
    return accountRepository.findByAccountType(type)
        .map(account -> new AccountResponse(account.getIdValue(), account.getAccountNumber(), account.getExternalAccountNumber(), account.getAccountType(), account.getStatus(), account.getBalance().getAmount(), account.getBalance().getCurrency().getCurrencyCode(), account.getCreatedAt(), account.getUpdatedAt()));
  }

  @Override
  public Flux<AccountResponse> findAllByStatus(AccountStatus status) {
    return accountRepository.findByStatus(status)
        .map(account -> new AccountResponse(account.getIdValue(), account.getAccountNumber(), account.getExternalAccountNumber(), account.getAccountType(), account.getStatus(), account.getBalance().getAmount(), account.getBalance().getCurrency().getCurrencyCode(), account.getCreatedAt(), account.getUpdatedAt()));
  }

  @Override
  public Flux<AccountResponse> findAllByCustomer(String customerId) {
    return accountRepository.findByCustomerId(customerId)
        .map(account -> new AccountResponse(account.getIdValue(), account.getAccountNumber(), account.getExternalAccountNumber(), account.getAccountType(), account.getStatus(), account.getBalance().getAmount(), account.getBalance().getCurrency().getCurrencyCode(), account.getCreatedAt(), account.getUpdatedAt()));
  }

  @Override
  public Flux<AccountResponse> findAllByTypeAndStatus(AccountType type, AccountStatus status) {
    return accountRepository.findByTypeAndStatus(type, status)
        .map(account -> new AccountResponse(account.getIdValue(), account.getAccountNumber(), account.getExternalAccountNumber(), account.getAccountType(), account.getStatus(), account.getBalance().getAmount(), account.getBalance().getCurrency().getCurrencyCode(), account.getCreatedAt(), account.getUpdatedAt()));
  }
}
