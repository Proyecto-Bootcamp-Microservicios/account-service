package com.NTTDATA.bootcamp.msvc_account.application.port.in;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountRequest;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import reactor.core.publisher.Mono;

public interface ICreateSavingAccountUseCase {
    Mono<AccountResponse> execute(CreateAccountRequest request);
}
