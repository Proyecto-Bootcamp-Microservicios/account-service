package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.ICreateFixedTermAccountUseCase;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IRetriveCustomerByIdPort;
import com.ntt.data.bootcamp.msvc.account.domain.FixedTermAccount;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
public class CreateFixedTermAccountServiceImpl implements ICreateFixedTermAccountUseCase {

  private final IRetriveCustomerByIdPort retriveCustomerByIdPort;
  private final IAccountRepositoryPort accountRepositoryPort;

  @Override
  public Mono<AccountResponse> createFixedTermAccount(CreateAccountCommand command) {
    return retriveCustomerByIdPort
        .retriveCustomerById(command.getCustomerId())
        .flatMap(
            customerResponse -> {
              if (customerResponse.getCustomerType().equals("PERSONAL")) {
                return accountRepositoryPort
                    .countAccountsByCustomerIdAndAccountType(customerResponse.getId(), "FIXED_TERM")
                    .flatMap(
                        count -> {
                          log.info("Count " + count);
                          if (count >= 1)
                            return Mono.error(
                                new IllegalArgumentException(
                                    "Customer already has a checking account"));
                          return Mono.just(customerResponse);
                        });
              }
              return Mono.just(customerResponse);
            })
        .flatMap(
            customerResponse -> {
              FixedTermAccount checkingAccount =
                  FixedTermAccount.ofSemiAnnually(
                      customerResponse.getId(),
                      customerResponse.getCustomerType(),
                      customerResponse.getDocumentType(),
                      customerResponse.getDocumentNumber(),
                      command.getAmount());
              return accountRepositoryPort.save(checkingAccount);
            })
        .map(
            account ->
                new AccountResponse(
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
