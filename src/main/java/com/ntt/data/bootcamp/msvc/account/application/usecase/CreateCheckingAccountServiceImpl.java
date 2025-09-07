package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.ICreateCheckingAccountUseCase;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IRetriveCustomerByIdPort;
import com.ntt.data.bootcamp.msvc.account.domain.CheckingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
public class CreateCheckingAccountServiceImpl implements ICreateCheckingAccountUseCase {

  private final IRetriveCustomerByIdPort retriveCustomerByIdPort;
  private final IAccountRepositoryPort accountRepositoryPort;

  @Override
  public Mono<AccountResponse> createCheckingAccountWithAmountZero(CreateAccountCommand request) {
    return retriveCustomerByIdPort
        .retriveCustomerById(request.getCustomerId())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
        .flatMap(
            customerResponse -> {
              if (customerResponse.getCustomerType().equals("PERSONAL")) {
                return accountRepositoryPort
                    .countAccountsByCustomerIdAndAccountType(customerResponse.getId(), "CHECKING")
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
              CheckingAccount checkingAccount =
                  CheckingAccount.of(
                      customerResponse.getId(),
                      customerResponse.getCustomerType(),
                      customerResponse.getDocumentType(),
                      customerResponse.getDocumentNumber());
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

  @Override
  public Mono<AccountResponse> createCheckingAccountWithCustomAmount(CreateAccountCommand request) {
    return retriveCustomerByIdPort
        .retriveCustomerById(request.getCustomerId())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
        .flatMap(
            customerResponse -> {
              if (customerResponse.getCustomerType().equals("PERSONAL")) {
                return accountRepositoryPort
                    .countAccountsByCustomerIdAndAccountType(customerResponse.getId(), "CHECKING")
                    .flatMap(
                        count -> {
                          log.error("Count " + count);
                          if (count >= 1) {
                            return Mono.error(
                                new IllegalArgumentException(
                                    "Customer already has a checking account"));
                          }
                          return Mono.just(customerResponse);
                        });
              }
              return Mono.just(customerResponse);
            })
        .flatMap(
            customerResponse -> {
              CheckingAccount checkingAccount =
                  CheckingAccount.of(
                      customerResponse.getId(),
                      customerResponse.getCustomerType(),
                      customerResponse.getDocumentType(),
                      customerResponse.getDocumentNumber(),
                      request.getAmount());
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
