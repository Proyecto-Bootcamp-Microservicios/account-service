package com.ntt.data.bootcamp.msvc.account.infrastructure.service;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.DailyBalanceSnapshotResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.IDailyBalancesQuery;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.DailyBalanceSnapshotCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringDailyBalanceSnapshotRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Comparator;

@Service
@AllArgsConstructor
public class DailyBalancesQueryService implements IDailyBalancesQuery {

  private final ISpringDailyBalanceSnapshotRepository repository;

  @Override
  public Flux<DailyBalanceSnapshotResponse> getCustomerMonthlyBalances(String customerId, int year, int month) {
    LocalDate from = LocalDate.of(year, month, 1);
    LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

    return repository.findByCustomerIdAndDateBetween(customerId, from, to)
        .sort(Comparator.comparing(DailyBalanceSnapshotCollection::getDate))
        .map(this::toResponse);
  }

  @Override
  public Flux<DailyBalanceSnapshotResponse> getAccountMonthlyBalances(String accountId, int year, int month) {
    LocalDate from = LocalDate.of(year, month, 1);
    LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

    return repository.findByAccountIdAndDateBetween(accountId, from, to)
        .sort(Comparator.comparing(DailyBalanceSnapshotCollection::getDate))
        .map(this::toResponse);
  }

  private DailyBalanceSnapshotResponse toResponse(DailyBalanceSnapshotCollection s) {
    return new DailyBalanceSnapshotResponse(
        s.getId(),
        s.getAccountId(),
        s.getCustomerId(),
        s.getAccountType(),
        s.getBalance(),
        s.getDate()
    );
  }
}