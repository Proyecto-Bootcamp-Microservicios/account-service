package com.ntt.data.bootcamp.msvc.account.application.port.in;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.CustomerBalanceReportResponse;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

public interface IGenerateCustomerBalanceReportUseCase {
  Mono<CustomerBalanceReportResponse> generateCustomerBalanceReport(String customerId, LocalDate month);
}