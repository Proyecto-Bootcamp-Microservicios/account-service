package com.ntt.data.bootcamp.msvc.account.infrastructure.scheduler;

import com.ntt.data.bootcamp.msvc.account.domain.Account;
import com.ntt.data.bootcamp.msvc.account.domain.CheckingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.PymeCheckingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationDirection;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationType;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Scheduler que cobra la cuota de mantenimiento a las cuentas checking el primer día de cada mes.
 * Excluye las cuentas PYME_CHECKING que tienen cuota cero.
 */
@Component
@AllArgsConstructor
@Slf4j
public class MaintenanceFeeScheduler {

  private final IAccountRepositoryPort accountRepository;

  /**
   * Se ejecuta el primer día de cada mes a las 9:00 AM para cobrar mantenimiento.
   * Solo procesa cuentas checking (excluyendo PYME_CHECKING).
   */
  @Scheduled(cron = "0 0 9 1 * ?") // Primer día de cada mes a las 9:00 AM
  public void chargeMaintenanceFee() {
    log.info("Iniciando cobro de mantenimiento para cuentas checking - {}", LocalDate.now());

    // Obtener todas las cuentas checking activas (excluyendo PYME_CHECKING)
    Flux<Account> checkingAccounts = accountRepository
        .findByAccountType(AccountType.CHECKING.name())
        .filter(account -> account.getStatus() == AccountStatus.ACTIVE);

    // Procesar cada cuenta
    checkingAccounts
        .flatMap(this::processMaintenanceFee)
        .doOnComplete(() -> log.info("Proceso de cobro de mantenimiento completado"))
        .doOnError(error -> log.error("Error en el proceso de cobro de mantenimiento: {}", error.getMessage()))
        .subscribe();
  }

  /**
   * Procesa el cobro de mantenimiento para una cuenta específica.
   */
  private Mono<Account> processMaintenanceFee(Account account) {
    if (!(account instanceof CheckingAccount)) {
      return Mono.just(account);
    }

    CheckingAccount checkingAccount = (CheckingAccount) account;

    // Verificar si es PYME_CHECKING (no cobrar mantenimiento)
    if (checkingAccount instanceof PymeCheckingAccount) {
      log.debug("Saltando cobro de mantenimiento para cuenta PYME: {}",
          checkingAccount.getAccountNumber());
      return Mono.just(account);
    }

    // Verificar si la cuota está vencida
    if (!checkingAccount.isMaintenanceFeeDue()) {
      log.debug("Cuota de mantenimiento no vencida para cuenta: {}",
          checkingAccount.getAccountNumber());
      return Mono.just(account);
    }

    // Usar el método del dominio que maneja la lógica de suspensión
    Account processedAccount = checkingAccount.processMaintenanceFeePayment();

    // Log del resultado
    if (processedAccount.getStatus() == AccountStatus.SUSPENDED) {
      log.warn("Cuenta {} suspendida por saldo insuficiente para mantenimiento",
          checkingAccount.getAccountNumber());
    } else {
      log.info("Cobro de mantenimiento aplicado a cuenta {} por ${}",
          checkingAccount.getAccountNumber(), checkingAccount.getMaintenanceFee());
    }

    // Guardar en base de datos
    return accountRepository.save(processedAccount);
  }
}