package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

/**
 * Use case for creating VIP saving accounts.
 */
public interface ICreateVipSavingAccountUseCase {
  /** Creates a new VIP saving account. */
  Mono<AccountResponse> createVipSavingAccount(CreateAccountCommand command);
}