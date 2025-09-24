package com.ntt.data.bootcamp.msvc.account.application.port.out;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.ProductEligibilityResponse;
import reactor.core.publisher.Mono;

public interface IValidateDebtPort {
  Mono<ProductEligibilityResponse> validateDebt(String customerId);
}
