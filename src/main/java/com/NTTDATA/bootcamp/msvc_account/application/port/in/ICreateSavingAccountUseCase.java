package com.NTTDATA.bootcamp.msvc_account.application.port.in;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountCommand;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

public interface ICreateSavingAccountUseCase {
    Mono<AccountResponse> createSavingAccount(CreateAccountCommand command);
}
