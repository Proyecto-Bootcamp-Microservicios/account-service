package com.ntt.data.bootcamp.msvc.account.infrastructure.scheduler;

import com.ntt.data.bootcamp.msvc.account.domain.Account;
import com.ntt.data.bootcamp.msvc.account.domain.VipSavingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.DailyBalanceSnapshotCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringDailyBalanceSnapshotRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class VipAccountMonthlyValidationScheduler {

  private final IAccountRepositoryPort accountRepository;
  private final ISpringDailyBalanceSnapshotRepository snapshotRepository;

  @Scheduled(cron = "0 0 1 1 * *") // Primer día de cada mes a las 00:00
  public void validateVipAccountsMonthlyAverage() {
    log.info("Starting VIP account monthly average validation for previous month");

    LocalDate previousMonth = LocalDate.now().minusMonths(1);
    LocalDate monthStart = previousMonth.withDayOfMonth(1);
    LocalDate monthEnd = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());

    accountRepository.findByAccountType("VIP_SAVING")
        .cast(VipSavingAccount.class)
        .flatMap(vipAccount -> validateAccountCompliance(vipAccount, monthStart, monthEnd))
        .doOnComplete(() -> log.info("VIP account validation completed"))
        .doOnError(error -> log.error("Error validating VIP accounts", error))
        .subscribe();
  }

  private Mono<VipSavingAccount> validateAccountCompliance(
      VipSavingAccount vipAccount, LocalDate monthStart, LocalDate monthEnd) {

    return snapshotRepository
        .findByAccountIdAndDateBetween(vipAccount.getIdValue(), monthStart, monthEnd)
        .collectList()
        .flatMap(snapshots -> {
          if (snapshots.isEmpty()) {
            log.warn("No snapshots found for VIP account {} in period {}-{}",
                vipAccount.getIdValue(), monthStart, monthEnd);
            return suspendAccount(vipAccount, "No daily balance data available");
          }

          BigDecimal monthlyAverage = calculateMonthlyAverage(snapshots);
          BigDecimal minimumRequired = vipAccount.getMinimumDailyAverage();

          log.info("VIP Account {} - Monthly Average: {}, Minimum Required: {}",
              vipAccount.getIdValue(), monthlyAverage, minimumRequired);

          if (monthlyAverage.compareTo(minimumRequired) < 0) {
            log.warn("VIP Account {} does not meet minimum daily average requirement. " +
                    "Average: {}, Required: {}",
                vipAccount.getIdValue(), monthlyAverage, minimumRequired);
            return suspendAccount(vipAccount,
                String.format("Monthly average %.2f below required %.2f",
                    monthlyAverage, minimumRequired));
          }

          log.info("VIP Account {} meets minimum daily average requirement", vipAccount.getIdValue());
          return Mono.just(vipAccount);
        });
  }

  private Mono<VipSavingAccount> suspendAccount(VipSavingAccount vipAccount, String reason) {
    log.warn("Suspending VIP account {} - Reason: {}", vipAccount.getIdValue(), reason);

    Account suspendedAccount = vipAccount.changeStatus(AccountStatus.SUSPENDED);
    return accountRepository.save(suspendedAccount)
        .cast(VipSavingAccount.class)
        .doOnNext(account ->
            log.info("VIP account {} has been suspended", account.getIdValue()));
  }

  private BigDecimal calculateMonthlyAverage(List<DailyBalanceSnapshotCollection> snapshots) {
    BigDecimal sum = snapshots.stream()
        .map(DailyBalanceSnapshotCollection::getBalance)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return sum.divide(BigDecimal.valueOf(snapshots.size()), RoundingMode.HALF_UP);
  }
}
