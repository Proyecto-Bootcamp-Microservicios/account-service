package com.NTTDATA.bootcamp.msvc_account.application.port.in;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountCommand;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

public interface ICreateCheckingAccountUseCase {
    Mono<AccountResponse> createCheckingAccountWithAmountZero(CreateAccountCommand request);
    Mono<AccountResponse> createCheckingAccountWithCustomAmount(CreateAccountCommand request);
}
