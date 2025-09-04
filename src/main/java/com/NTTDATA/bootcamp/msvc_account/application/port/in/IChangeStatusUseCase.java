package com.NTTDATA.bootcamp.msvc_account.application.port.in;

import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import reactor.core.publisher.Mono;

public interface IChangeStatusUseCase {
    Mono<AccountResponse> changeStatus(String accountId, AccountStatus status);
}
