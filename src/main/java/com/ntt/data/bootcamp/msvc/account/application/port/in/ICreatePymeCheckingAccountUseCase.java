package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

/**
 * Use case for creating PYME checking accounts.
 */
public interface ICreatePymeCheckingAccountUseCase {
  /** Creates a new PYME checking account. */
  Mono<AccountResponse> createPymeCheckingAccount(CreateAccountCommand command);
}