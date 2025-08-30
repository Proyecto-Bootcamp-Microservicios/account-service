package com.NTTDATA.bootcamp.msvc_account.application.port.out;

import com.NTTDATA.bootcamp.msvc_account.application.dto.response.CustomerResponse;
import reactor.core.publisher.Mono;

public interface IRetriveCustomerByIdPort {
    Mono<CustomerResponse> retriveCustomerById(String id);
}
