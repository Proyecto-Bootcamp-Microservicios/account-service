package com.NTTDATA.bootcamp.msvc_account.application.usecase;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountCommand;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateSavingAccountUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.out.IRetriveCustomerByIdPort;
import com.NTTDATA.bootcamp.msvc_account.domain.SavingAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
public class CreateSavingAccountServiceImpl implements ICreateSavingAccountUseCase {

    private final IRetriveCustomerByIdPort retriveCustomerByIdPort;
    private final IAccountRepositoryPort accountRepositoryPort;

    @Override
    public Mono<AccountResponse> createSavingAccount(CreateAccountCommand command) {
        return retriveCustomerByIdPort.retriveCustomerById(command.getCustomerId())
                .switchIfEmpty(Mono.error(new Exception("Customer not found")))
                .flatMap(customerResponse -> {
                    if (customerResponse.getCustomerType().equals("PERSONAL")) {
                        return accountRepositoryPort.countAccountsByCustomerIdAndAccountType(customerResponse.getId(), "SAVING")
                                .flatMap(count -> {
                                    log.info("Count " + count);
                                    if (count >= 1) return Mono.error(new IllegalArgumentException("Customer already has a checking account"));
                                    return Mono.just(customerResponse);
                                });
                    }
                    return Mono.just(customerResponse);
                })
                .flatMap(customer -> {
                    SavingAccount savingAccount = SavingAccount.of(command.getCustomerId(), customer.getCustomerType(), customer.getDocumentType(), customer.getDocumentNumber());
                    return accountRepositoryPort.save(savingAccount);
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
                        account.getUpdatedAt()));
    }
}
