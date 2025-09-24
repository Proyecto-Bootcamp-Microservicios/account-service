package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.ICreateVipSavingAccountUseCase;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IRetriveCustomerByIdPort;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IValidateDebtPort;
import com.ntt.data.bootcamp.msvc.account.domain.VipSavingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Application service to create VIP saving accounts.
 */
@Slf4j
@AllArgsConstructor
public class CreateVipSavingAccountServiceImpl implements ICreateVipSavingAccountUseCase {

  private final IRetriveCustomerByIdPort retriveCustomerByIdPort;
  private final IAccountRepositoryPort accountRepositoryPort;
  private final IValidateDebtPort validateDebtPort;

  /** Creates a new VIP saving account for the given customer. */
  @Override
  public Mono<AccountResponse> createVipSavingAccount(CreateAccountCommand command) {
    return retriveCustomerByIdPort
        .retriveCustomerById(command.getCustomerId())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
        .flatMap(
            customerResponse -> {
              // Validar que el cliente sea PERSONAL
              if (!customerResponse.getCustomerType().equals("PERSONAL")) {
                return Mono.error(
                    new IllegalArgumentException("VIP saving accounts are only for personal customers"));
              }

              // Validar que el cliente tenga perfil VIP
              if (!customerResponse.getCustomerProfile().equals("VIP")) {
                return Mono.error(
                    new IllegalArgumentException("Only VIP customers can create VIP saving accounts"));
              }

              // Validar que no tenga ya una cuenta VIP de ahorro
              return accountRepositoryPort
                  .countAccountsByCustomerIdAndAccountType(customerResponse.getId(), "VIP_SAVING")
                  .flatMap(
                      count -> {
                        log.info("VIP Saving Account count for customer {}: {}", customerResponse.getId(), count);
                        if (count >= 1) {
                          return Mono.error(
                              new IllegalArgumentException("Customer already has a VIP saving account"));
                        }
                        return Mono.just(customerResponse);
                      });
            })
        .flatMap(customerResponse ->
            validateCustomerDebt(customerResponse.getId())
                .then(Mono.just(customerResponse)))
        .flatMap(
            customer -> {
              VipSavingAccount vipSavingAccount = VipSavingAccount.of(
                  command.getCustomerId(),
                  customer.getCustomerType(),
                  customer.getDocumentType(),
                  customer.getDocumentNumber());
              return accountRepositoryPort.save(vipSavingAccount);
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