package com.NTTDATA.bootcamp.msvc_account.application.usecase;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountRequest;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateCheckingAccountUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.out.IRetriveCustomerByIdPort;
import com.NTTDATA.bootcamp.msvc_account.domain.CheckingAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.ICheckingAccountRepositoryPort;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class CreateCheckingAccountServiceImpl implements ICreateCheckingAccountUseCase {

    private final ICheckingAccountRepositoryPort checkingAccountRepositoryPort;
    private final IRetriveCustomerByIdPort retriveCustomerByIdPort;

    @Override
    public Mono<AccountResponse> createCheckingAccountWithAmountZero(CreateAccountRequest request) {
        return retriveCustomerByIdPort.retriveCustomerById(request.getCustomerId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                .flatMap(customerResponse -> {
                    CheckingAccount checkingAccount = CheckingAccount.of(
                            customerResponse.getId(),
                            customerResponse.getCustomerType(),
                            request.getDocumentType(),
                            customerResponse.getDocumentNumber());
                    return checkingAccountRepositoryPort.save(checkingAccount);
                })
                .map(account -> new AccountResponse(
                    account.getIdValue(),
                    account.getAccountNumber(),
                    account.getExternalAccountNumber(),
                    account.getAccountType(),
                    account.getStatus(),
                    account.getBalance().getAmount(),
                    account.getBalance().getCurrency().getCurrencyCode(),
                    account.getCreatedAt(),
                    account.getUpdatedAt())
                );
    }

    @Override
    public Mono<AccountResponse> createCheckingAccountWithCustomAmount(CreateAccountRequest request) {
        return retriveCustomerByIdPort.retriveCustomerById(request.getCustomerId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                .flatMap(customerResponse -> {
                    CheckingAccount checkingAccount = CheckingAccount.of(
                            customerResponse.getId(),
                            customerResponse.getCustomerType(),
                            request.getDocumentType(),
                            customerResponse.getDocumentNumber(),
                            request.getAmount());
                    return checkingAccountRepositoryPort.save(checkingAccount);
                })
                .map(account -> new AccountResponse(
                        account.getIdValue(),
                        account.getAccountNumber(),
                        account.getExternalAccountNumber(),
                        account.getAccountType(),
                        account.getStatus(),
                        account.getBalance().getAmount(),
                        account.getBalance().getCurrency().getCurrencyCode(),
                        account.getCreatedAt(),
                        account.getUpdatedAt())
                );
    }
}
