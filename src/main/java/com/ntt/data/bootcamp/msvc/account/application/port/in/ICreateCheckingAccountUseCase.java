package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

public interface ICreateCheckingAccountUseCase {
  Mono<AccountResponse> createCheckingAccountWithAmountZero(CreateAccountCommand request);

  Mono<AccountResponse> createCheckingAccountWithCustomAmount(CreateAccountCommand request);
}
