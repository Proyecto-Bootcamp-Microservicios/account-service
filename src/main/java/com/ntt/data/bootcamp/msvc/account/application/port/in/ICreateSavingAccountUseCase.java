package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

/**
 * Use case for creating saving accounts.
 */
public interface ICreateSavingAccountUseCase {
  /** Creates a new saving account. */
  Mono<AccountResponse> createSavingAccount(CreateAccountCommand command);
}
