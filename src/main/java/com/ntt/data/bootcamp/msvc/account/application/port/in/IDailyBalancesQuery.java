package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.DailyBalanceSnapshotResponse;
import reactor.core.publisher.Flux;

public interface IDailyBalancesQuery {
  Flux<DailyBalanceSnapshotResponse> getCustomerMonthlyBalances(String customerId, int year, int month);
  Flux<DailyBalanceSnapshotResponse> getAccountMonthlyBalances(String accountId, int year, int month);
}
