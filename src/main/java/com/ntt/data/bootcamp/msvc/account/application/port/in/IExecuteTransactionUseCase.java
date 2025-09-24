package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.TransactionExecutionCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.TransactionExecuteResponse;
import reactor.core.publisher.Mono;

/**
 * Use case for executing a transaction over an account.
 */
public interface IExecuteTransactionUseCase {
  /** Executes a transaction identified by account number. */
  Mono<TransactionExecuteResponse> executeTransaction(
      String accountNumber, TransactionExecutionCommand command);
}
