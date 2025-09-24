package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import reactor.core.publisher.Mono;

/**
 * Use case for changing the status of an account.
 */
public interface IChangeStatusUseCase {
  /** Changes the status of the given account. */
  Mono<AccountResponse> changeStatus(String accountId, AccountStatus status);
}
