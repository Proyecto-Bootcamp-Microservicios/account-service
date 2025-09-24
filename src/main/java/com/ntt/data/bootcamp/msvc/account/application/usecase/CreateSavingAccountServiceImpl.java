package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.ICreateSavingAccountUseCase;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IRetriveCustomerByIdPort;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IValidateDebtPort;
import com.ntt.data.bootcamp.msvc.account.domain.SavingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Application service to create saving accounts.
 */
@Slf4j
@AllArgsConstructor
public class CreateSavingAccountServiceImpl implements ICreateSavingAccountUseCase {

  private final IRetriveCustomerByIdPort retriveCustomerByIdPort;
  private final IAccountRepositoryPort accountRepositoryPort;
  private final IValidateDebtPort validateDebtPort;

  /** Creates a new saving account for the given customer. */
  @Override
  public Mono<AccountResponse> createSavingAccount(CreateAccountCommand command) {
    return retriveCustomerByIdPort
        .retriveCustomerById(command.getCustomerId())
        .switchIfEmpty(Mono.error(new Exception("Customer not found")))
        .flatMap(
            customerResponse -> {
              if (customerResponse.getCustomerType().equals("PERSONAL")) {
                return accountRepositoryPort
                    .countAccountsByCustomerIdAndAccountType(customerResponse.getId(), "SAVING")
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
        .flatMap(customerResponse ->
            validateCustomerDebt(customerResponse.getId())
                .then(Mono.just(customerResponse)))
        .flatMap(
            customer -> {
              SavingAccount savingAccount =
                  SavingAccount.of(
                      command.getCustomerId(),
                      customer.getCustomerType(),
                      customer.getDocumentType(),
                      customer.getDocumentNumber());
              return accountRepositoryPort.save(savingAccount);
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

  private Mono<Void> validateCustomerDebt(String customerId) {
    return validateDebtPort.validateDebt(customerId)
        .flatMap(eligibilityResponse -> {
          if (eligibilityResponse.getIsEligible() == null || !eligibilityResponse.getIsEligible()) {
            String reason = eligibilityResponse.getReason() != null ?
                eligibilityResponse.getReason() : "Customer has overdue debts";
            return Mono.error(new IllegalArgumentException(
                "Customer cannot create account due to debt: " + reason));
          }
          return Mono.empty();
        })
        .switchIfEmpty(Mono.error(new IllegalArgumentException(
            "Unable to validate customer debt status"))).then();
  }
}
