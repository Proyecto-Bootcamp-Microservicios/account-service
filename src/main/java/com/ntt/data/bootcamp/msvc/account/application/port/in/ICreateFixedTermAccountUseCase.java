package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

/**
 * Use case for creating fixed-term accounts.
 */
public interface ICreateFixedTermAccountUseCase {
  /** Creates a new fixed-term account. */
  Mono<AccountResponse> createFixedTermAccount(CreateAccountCommand command);
}
