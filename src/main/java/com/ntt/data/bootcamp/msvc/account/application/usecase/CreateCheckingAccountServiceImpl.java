package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.ICreateCheckingAccountUseCase;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IRetriveCustomerByIdPort;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IValidateDebtPort;
import com.ntt.data.bootcamp.msvc.account.domain.CheckingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Application service to create checking accounts respecting business constraints.
 */
@Slf4j
@AllArgsConstructor
public class CreateCheckingAccountServiceImpl implements ICreateCheckingAccountUseCase {

  private final IRetriveCustomerByIdPort retriveCustomerByIdPort;
  private final IAccountRepositoryPort accountRepositoryPort;
  private final IValidateDebtPort validateDebtPort;

  /** Creates a checking account with zero initial amount. */
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
        .flatMap(customerResponse ->
            validateCustomerDebt(customerResponse.getId())
                .then(Mono.just(customerResponse)))
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
