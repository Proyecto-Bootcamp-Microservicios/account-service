package com.ntt.data.bootcamp.msvc.account.infrastructure.adapter.in.rest;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.DailyBalanceSnapshotResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.IDailyBalancesQuery;
import com.ntt.data.bootcamp.msvc.account.application.port.in.IGenerateCustomerBalanceReportUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.CustomerBalanceReportResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/daily-balances")
@AllArgsConstructor
/**
 * REST controller exposing read-only endpoints for daily balance snapshots.
 * Intended for reporting/analytics use cases.
 */
public class DailyBalanceController {

  private final IGenerateCustomerBalanceReportUseCase customerBalanceReportUseCase;
  private final IDailyBalancesQuery dailyBalancesQuery;

  /**
   * Retrieves all daily balance snapshots for a customer within a month.
   *
   * @param customerId customer identifier
   * @param year target year
   * @param month target month (1-12)
   * @return Mono with balances found in the given period
   */
  @GetMapping("/customer/{customerId}/month/{year}/{month}")
  public Mono<ResponseEntity<Flux<DailyBalanceSnapshotResponse>>> getCustomerMonthlyBalances(
      @PathVariable String customerId,
      @PathVariable int year,
      @PathVariable int month) {

    return Mono.just(ResponseEntity.ok(
        dailyBalancesQuery.getCustomerMonthlyBalances(customerId, year, month)
    ));
  }

  /**
   * Retrieves all daily balance snapshots for an account within a month.
   *
   * @param accountId account identifier
   * @param year target year
   * @param month target month (1-12)
   * @return Mono with balances found in the given period
   */
  @GetMapping("/account/{accountId}/month/{year}/{month}")
  public Mono<ResponseEntity<Flux<DailyBalanceSnapshotResponse>>> getAccountMonthlyBalances(
      @PathVariable String accountId,
      @PathVariable int year,
      @PathVariable int month) {

    return Mono.just(ResponseEntity.ok(
        dailyBalancesQuery.getAccountMonthlyBalances(accountId, year, month)
    ));
  }

  @GetMapping("/customer/{customerId}/balance-summary/{year}/{month}")
  public Mono<ResponseEntity<CustomerBalanceReportResponse>> getCustomerBalanceSummary(
      @PathVariable String customerId,
      @PathVariable int year,
      @PathVariable int month) {

    LocalDate targetMonth = LocalDate.of(year, month, 1);

    return customerBalanceReportUseCase.generateCustomerBalanceReport(customerId, targetMonth)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}