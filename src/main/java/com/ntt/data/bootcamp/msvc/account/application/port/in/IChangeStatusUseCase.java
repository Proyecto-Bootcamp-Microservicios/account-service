package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import reactor.core.publisher.Mono;

public interface IChangeStatusUseCase {
  Mono<AccountResponse> changeStatus(String accountId, AccountStatus status);
}
