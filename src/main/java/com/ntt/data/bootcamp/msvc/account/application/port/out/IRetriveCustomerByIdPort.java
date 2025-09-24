package com.ntt.data.bootcamp.msvc.account.application.port.out;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.CustomerResponse;
import reactor.core.publisher.Mono;

/**
 * Outbound port for retrieving customer information from external service.
 */
public interface IRetriveCustomerByIdPort {
  /** Retrieves the customer by its identifier. */
  Mono<CustomerResponse> retriveCustomerById(String id);
}
