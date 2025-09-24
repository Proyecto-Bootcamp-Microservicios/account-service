// src/main/java/com/ntt/data/bootcamp/msvc/account/application/usecase/CustomerBalanceReportServiceImpl.java
package com.ntt.data.bootcamp.msvc.account.application.usecase;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.CustomerBalanceReportResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.IGenerateCustomerBalanceReportUseCase;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.DailyBalanceSnapshotCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringDailyBalanceSnapshotRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class CustomerBalanceReportServiceImpl implements IGenerateCustomerBalanceReportUseCase {

  private final ISpringDailyBalanceSnapshotRepository dailyBalanceRepository;

  @Override
  public Mono<CustomerBalanceReportResponse> generateCustomerBalanceReport(String customerId, LocalDate month) {
    LocalDate monthStart = month.withDayOfMonth(1);
    LocalDate monthEnd = month.withDayOfMonth(month.lengthOfMonth());

    return dailyBalanceRepository.findByCustomerIdAndDateBetween(customerId, monthStart, monthEnd)
        .collectList()
        .map(snapshots -> {
          if (snapshots.isEmpty()) {
            return new CustomerBalanceReportResponse(
                customerId, month, Map.of(), BigDecimal.ZERO, "PEN", 0);
          }

          // Agrupar por tipo de cuenta
          Map<AccountType, List<DailyBalanceSnapshotCollection>> groupedByType =
              snapshots.stream()
                  .collect(Collectors.groupingBy(
                      snapshot -> AccountType.valueOf(snapshot.getAccountType())));

          // Calcular promedio por tipo de cuenta
          Map<AccountType, BigDecimal> averageBalancesByProduct = groupedByType.entrySet()
              .stream()
              .collect(Collectors.toMap(
                  Map.Entry::getKey,
                  entry -> calculateAverage(entry.getValue())));

          // Calcular promedio total
          BigDecimal totalAverage = calculateAverage(snapshots);

          // Obtener moneda (asumiendo que todas las cuentas usan la misma moneda)
          String currency = snapshots.get(0).getCurrency();

          // Contar cuentas únicas
          int totalAccounts = (int) snapshots.stream()
              .map(DailyBalanceSnapshotCollection::getAccountId)
              .distinct()
              .count();

          return new CustomerBalanceReportResponse(
              customerId, month, averageBalancesByProduct, totalAverage, currency, totalAccounts);
        })
        .doOnSuccess(report -> log.info("Balance report generated for customer: {}", customerId))
        .doOnError(error -> log.error("Error generating balance report: {}", error.getMessage()));
  }

  private BigDecimal calculateAverage(List<DailyBalanceSnapshotCollection> snapshots) {
    if (snapshots.isEmpty()) return BigDecimal.ZERO;

    BigDecimal sum = snapshots.stream()
        .map(DailyBalanceSnapshotCollection::getBalance)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return sum.divide(BigDecimal.valueOf(snapshots.size()), RoundingMode.HALF_UP);
  }
}