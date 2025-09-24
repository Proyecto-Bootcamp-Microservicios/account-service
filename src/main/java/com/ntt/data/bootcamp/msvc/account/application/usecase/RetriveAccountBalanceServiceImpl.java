package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.BalanceResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.IRetriveAccountBalanceUseCase;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Application service to retrieve the balance of an account.
 */
@AllArgsConstructor
public class RetriveAccountBalanceServiceImpl implements IRetriveAccountBalanceUseCase {

  private final IAccountRepositoryPort accountRepositoryPort;

  @Override
  public Mono<BalanceResponse> retriveAccountBalance(String accountId) {
    return accountRepositoryPort
        .findById(accountId)
        .map(
            balance ->
                new BalanceResponse(accountId, balance.getAmount(), balance.getCurrencyCode()));
  }
}
