package com.NTTDATA.bootcamp.msvc_account.application.usecase;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountRequest;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateSavingAccountUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.out.IRetriveCustomerByIdPort;
import com.NTTDATA.bootcamp.msvc_account.domain.SavingAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.ISavingAccountRepositoryPort;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class CreateSavingAccountServiceImpl implements ICreateSavingAccountUseCase {

    private final ISavingAccountRepositoryPort savingAccountRepositoryPort;
    private final IRetriveCustomerByIdPort retriveCustomerByIdPort;

    @Override
    public Mono<AccountResponse> execute(CreateAccountRequest request) {
        return retriveCustomerByIdPort.retriveCustomerById(request.getCustomerId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                .flatMap(customerResponse -> savingAccountRepositoryPort.save(SavingAccount.of(
                        customerResponse.getId(),
                        customerResponse.getCustomerType(),
                        request.getDocumentType(),
                        customerResponse.getDocumentNumber())))
                .map(account -> new AccountResponse(
                        account.getIdValue(),
                        account.getAccountNumber(),
                        account.getExternalAccountNumber(),
                        account.getAccountType(),
                        account.getStatus(),
                        account.getBalance().getAmount(),
                        account.getBalance().getCurrency().getCurrencyCode(),
                        account.getCreatedAt(),
                        account.getUpdatedAt()));
    }
}
