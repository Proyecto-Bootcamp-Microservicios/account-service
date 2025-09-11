package com.ntt.data.bootcamp.msvc.account.infrastructure.adapter.in.rest;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.DailyBalanceSnapshotCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringDailyBalanceSnapshotRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/daily-balances")
@AllArgsConstructor
public class DailyBalanceController {

  private final ISpringDailyBalanceSnapshotRepository dailyBalanceRepository;

  @GetMapping("/customer/{customerId}/month/{year}/{month}")
  public Mono<ResponseEntity<Flux<DailyBalanceSnapshotCollection>>> getCustomerMonthlyBalances(
      @PathVariable String customerId,
      @PathVariable int year,
      @PathVariable int month) {

    LocalDate from = LocalDate.of(year, month, 1);
    LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

    return Mono.just(ResponseEntity.ok(
        dailyBalanceRepository.findByCustomerIdAndDateBetween(customerId, from, to)
    ));
  }

  @GetMapping("/account/{accountId}/month/{year}/{month}")
  public Mono<ResponseEntity<Flux<DailyBalanceSnapshotCollection>>> getAccountMonthlyBalances(
      @PathVariable String accountId,
      @PathVariable int year,
      @PathVariable int month) {

    LocalDate from = LocalDate.of(year, month, 1);
    LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

    return Mono.just(ResponseEntity.ok(
        dailyBalanceRepository.findByAccountIdAndDateBetween(accountId, from, to)
    ));
  }

  @PostMapping("/capture-now")
  public Mono<String> captureBalancesNow() {
    // Endpoint de prueba para capturar balances manualmente
    return Mono.just("Balance capture triggered - check logs");
  }
}