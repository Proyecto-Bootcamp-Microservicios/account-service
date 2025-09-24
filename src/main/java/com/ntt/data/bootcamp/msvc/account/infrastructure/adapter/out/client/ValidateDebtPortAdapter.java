package com.ntt.data.bootcamp.msvc.account.infrastructure.adapter.out.client;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.ProductEligibilityResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IValidateDebtPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ValidateDebtPortAdapter implements IValidateDebtPort {

  private final WebClient.Builder webClientBuilder;

  @Override
  public Mono<ProductEligibilityResponse> validateDebt(String customerId) {
    return webClientBuilder
        .baseUrl("http://credit-service/api/v1/credit-cards/customers")
        .build()
        .get()
        .uri("/{customerId}/product-eligibility", customerId)
        .retrieve()
        .bodyToMono(ProductEligibilityResponse.class)
        .switchIfEmpty(Mono.empty());
  }
}
