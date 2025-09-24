package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.ICreatePymeCheckingAccountUseCase;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IRetriveCustomerByIdPort;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IValidateDebtPort;
import com.ntt.data.bootcamp.msvc.account.domain.PymeCheckingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Application service to create PYME checking accounts.
 */
@Slf4j
@AllArgsConstructor
public class CreatePymeCheckingAccountServiceImpl implements ICreatePymeCheckingAccountUseCase {

  private final IRetriveCustomerByIdPort retriveCustomerByIdPort;
  private final IAccountRepositoryPort accountRepositoryPort;
  private final IValidateDebtPort validateDebtPort;

  /** Creates a new PYME checking account for the given customer. */
  @Override
  public Mono<AccountResponse> createPymeCheckingAccount(CreateAccountCommand command) {
    return retriveCustomerByIdPort
        .retriveCustomerById(command.getCustomerId())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
        .flatMap(
            customerResponse -> {
              // Validar que el cliente sea ENTERPRISE
              if (!customerResponse.getCustomerType().equals("ENTERPRISE")) {
                return Mono.error(
                    new IllegalArgumentException("PYME checking accounts are only for enterprise customers"));
              }

              // Validar que el cliente tenga perfil PYME
              if (!customerResponse.getCustomerProfile().equals("PYME")) {
                return Mono.error(
                    new IllegalArgumentException("Only PYME customers can create PYME checking accounts"));
              }

              // Validar que no tenga ya una cuenta PYME de corriente
              return accountRepositoryPort
                  .countAccountsByCustomerIdAndAccountType(customerResponse.getId(), "PYME_CHECKING")
                  .flatMap(
                      count -> {
                        log.info("PYME Checking Account count for customer {}: {}", customerResponse.getId(), count);
                        if (count >= 1) {
                          return Mono.error(
                              new IllegalArgumentException("Customer already has a PYME checking account"));
                        }
                        return Mono.just(customerResponse);
                      });
            })
        .flatMap(customerResponse ->
            validateCustomerDebt(customerResponse.getId())
                .then(Mono.just(customerResponse)))
        .flatMap(
            customer -> {
              PymeCheckingAccount pymeCheckingAccount = PymeCheckingAccount.of(
                  command.getCustomerId(),
                  customer.getCustomerType(),
                  customer.getDocumentType(),
                  customer.getDocumentNumber());
              return accountRepositoryPort.save(pymeCheckingAccount);
            })
        .map(
            account -> new AccountResponse(
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