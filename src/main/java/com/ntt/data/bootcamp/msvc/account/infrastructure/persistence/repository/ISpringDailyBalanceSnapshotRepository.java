package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.DailyBalanceSnapshotCollection;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

/**
 * Reactive repository for daily balance snapshots used in reporting.
 */
public interface ISpringDailyBalanceSnapshotRepository extends ISpringGenericRepository<DailyBalanceSnapshotCollection, String> {
  /** Finds snapshots for an account within a date range. */
  Flux<DailyBalanceSnapshotCollection> findByAccountIdAndDateBetween(String accountId, LocalDate from, LocalDate to);
  /** Finds snapshots for a customer within a date range. */
  Flux<DailyBalanceSnapshotCollection> findByCustomerIdAndDateBetween(String customerId, LocalDate from, LocalDate to);
  /** Finds snapshots across all accounts within a date range. */
  Flux<DailyBalanceSnapshotCollection> findByDateBetween(LocalDate from, LocalDate to);
}
