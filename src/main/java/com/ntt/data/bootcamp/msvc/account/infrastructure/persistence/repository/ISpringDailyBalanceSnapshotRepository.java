package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.DailyBalanceSnapshotCollection;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface ISpringDailyBalanceSnapshotRepository extends ISpringGenericRepository<DailyBalanceSnapshotCollection, String> {
  Flux<DailyBalanceSnapshotCollection> findByAccountIdAndDateBetween(String accountId, LocalDate from, LocalDate to);
  Flux<DailyBalanceSnapshotCollection> findByCustomerIdAndDateBetween(String customerId, LocalDate from, LocalDate to);
  Flux<DailyBalanceSnapshotCollection> findByDateBetween(LocalDate from, LocalDate to);
}
