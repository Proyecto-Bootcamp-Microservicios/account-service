package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.TransactionExecutionCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.TransactionExecuteResponse;
import reactor.core.publisher.Mono;

public interface IExecuteTransactionUseCase {
  Mono<TransactionExecuteResponse> executeTransaction(
      String accountNumber, TransactionExecutionCommand command);
}
