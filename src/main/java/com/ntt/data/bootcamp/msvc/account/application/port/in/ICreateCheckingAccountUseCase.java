package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

/**
 * Use case for creating checking accounts.
 */
public interface ICreateCheckingAccountUseCase {
  /** Creates a checking account with zero initial balance. */
  Mono<AccountResponse> createCheckingAccountWithAmountZero(CreateAccountCommand request);

}
