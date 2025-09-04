package com.NTTDATA.bootcamp.msvc_account.application.port.in;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.TransactionExecutionCommand;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.TransactionExecuteResponse;
import reactor.core.publisher.Mono;

public interface IExecuteTransactionUseCase {
    Mono<TransactionExecuteResponse> executeTransaction(String accountId, TransactionExecutionCommand command);
}
