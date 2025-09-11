package com.ntt.data.bootcamp.msvc.account.infrastructure.scheduler;

import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.DailyBalanceSnapshotCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringDailyBalanceSnapshotRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class DailyBalanceScheduler {

  private final IAccountRepositoryPort accountRepository;
  private final ISpringDailyBalanceSnapshotRepository dailyBalanceRepository;

  @Scheduled(cron = "0 59 23 * * ?") // 23:59 cada día
  public void captureDailyBalances() {
    log.info("Starting daily balance capture for {}", LocalDate.now());

    accountRepository.findAll()
        .flatMap(account -> {
          DailyBalanceSnapshotCollection snapshot = DailyBalanceSnapshotCollection.builder()
              .id(UUID.randomUUID().toString())
              .accountId(account.getIdValue())
              .customerId(account.getCustomerId())
              .accountType(account.getAccountType().name())
              .balance(account.getBalance().getAmount())
              .currency(account.getBalance().getCurrency().toString())
              .date(LocalDate.now())
              .capturedAt(LocalDateTime.now())
              .build();

          return dailyBalanceRepository.save(snapshot);
        })
        .doOnNext(saved -> log.info("Daily balance captured for account: {}", saved.getAccountId()))
        .doOnComplete(() -> log.info("Daily balance capture completed"))
        .doOnError(error -> log.error("Error capturing daily balances: {}", error.getMessage()))
        .subscribe();
  }
}