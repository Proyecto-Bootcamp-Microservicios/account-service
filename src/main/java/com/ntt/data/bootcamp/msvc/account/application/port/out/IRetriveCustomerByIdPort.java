package com.ntt.data.bootcamp.msvc.account.application.port.out;

import com.ntt.data.bootcamp.msvc.account.application.dto.response.CustomerResponse;
import reactor.core.publisher.Mono;

public interface IRetriveCustomerByIdPort {
  Mono<CustomerResponse> retriveCustomerById(String id);
}
