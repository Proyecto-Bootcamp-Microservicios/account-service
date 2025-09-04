package com.NTTDATA.bootcamp.msvc_account.application.usecase;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.TransactionExecutionCommand;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.TransactionExecuteResponse;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.IExecuteTransactionUseCase;
import com.NTTDATA.bootcamp.msvc_account.domain.Account;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.IAccountRepositoryPort;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Balance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class ExecuteTransactionServiceImpl implements IExecuteTransactionUseCase {

    private final IAccountRepositoryPort accountRepositoryPort;

    @Override
    public Mono<TransactionExecuteResponse> executeTransaction(String accountId, TransactionExecutionCommand command) {
        return accountRepositoryPort.findById(accountId)
                .switchIfEmpty(Mono.error(new Exception("Account not found")))
                .flatMap(account -> {
                    account.validateTransaction(command.getOperationType(), command.getAmount());

                    BigDecimal commission = account.getCommissionPerType(command.getOperationType(), command.getAmount());
                    BigDecimal amountWithCommission = account.calculateAmountWithCommission(command.getOperationType(), command.getAmount());
                    BigDecimal previousBalance = account.getBalance().getAmount();

                    Balance balanceUpdated = account.calculateNewBalance(command.getOperationType(), amountWithCommission);

                    Account accountUpdated = account
                            .withNewBalance(balanceUpdated)
                            .withNewTransactionLimit(account.incrementCurrentTransaction(command.getOperationType()));

                    return accountRepositoryPort.save(accountUpdated)
                            .doOnSuccess(accountPersisted -> log.info("Account updated: {}", accountPersisted.getIdValue()))
                            .doOnSuccess(accountPersisted -> log.info("Transaction success: {}", command.getTransactionId()))
                            .doOnError(error -> log.error("Error updating account: {}", error.getMessage()))
                            .map(accountPersisted -> createResponse(accountPersisted, command, commission, previousBalance));
                });
    }

    private TransactionExecuteResponse createResponse(Account account, TransactionExecutionCommand command,
                                                      BigDecimal commission, BigDecimal previousBalance) {
        return new TransactionExecuteResponse(
                account.getIdValue(),
                account.getAccountType().name(),
                account.getBalance().getAmount(),
                previousBalance,
                Map.of(command.getOperationType().name(), account.getTransactionLimit().getCurrentTransactionsPerType(command.getOperationType())),
                commission,
                "Transaction executed successfully",
                account.getDisplayAmount(),
                "",
                command.getTransactionId(),
                account.getUpdatedAt().toString(),
                new TransactionExecuteResponse.TransactionLimitInfo(
                        Map.of(command.getOperationType().name(), account.getTransactionLimit().getFreeTransactionsPerType(command.getOperationType())),
                        Map.of(command.getOperationType().name(), account.remainingFreeMovements(command.getOperationType())),
                        account.isFreeTransaction(command.getOperationType())
                )
        );
    }
}
