package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.IChangeStatusUseCase;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ChangeStatusServiceImpl implements IChangeStatusUseCase {

  private final IAccountRepositoryPort accountRepositoryPort;

  @Override
  public Mono<AccountResponse> changeStatus(String accountId, AccountStatus status) {
    return accountRepositoryPort
        .findById(accountId)
        .switchIfEmpty(Mono.error(new Exception("Account not found")))
        .flatMap(account -> accountRepositoryPort.save(account.changeStatus(status)))
        .map(
            account ->
                new AccountResponse(
                    account.getIdValue(),
                    account.getAccountNumber(),
                    account.getExternalAccountNumber(),
                    account.getAccountType(),
                    account.getStatus(),
                    account.getBalance().getAmount(),
                    account.getBalance().getCurrency().getCurrencyCode(),
                    account.getCreatedAt(),
                    account.getUpdatedAt()));
  }
}
