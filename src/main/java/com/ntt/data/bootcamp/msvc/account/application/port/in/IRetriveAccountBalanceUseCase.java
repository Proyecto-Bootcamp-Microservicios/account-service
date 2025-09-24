package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.BalanceResponse;
import reactor.core.publisher.Mono;

/**
 * Use case for retrieving the balance of an account.
 */
public interface IRetriveAccountBalanceUseCase {
  /** Retrieves current balance by account id. */
  Mono<BalanceResponse> retriveAccountBalance(String accountId);
}
