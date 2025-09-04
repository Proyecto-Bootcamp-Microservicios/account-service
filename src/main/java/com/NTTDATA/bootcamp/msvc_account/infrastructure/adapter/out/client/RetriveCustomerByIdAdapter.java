package com.NTTDATA.bootcamp.msvc_account.infrastructure.adapter.out.client;

import com.NTTDATA.bootcamp.msvc_account.application.dto.response.CustomerResponse;
import com.NTTDATA.bootcamp.msvc_account.application.port.out.IRetriveCustomerByIdPort;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RetriveCustomerByIdAdapter implements IRetriveCustomerByIdPort {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<CustomerResponse> retriveCustomerById(String id) {
        return webClientBuilder
                .baseUrl("http://msvc-customer/customers")
                .build()
                .get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .switchIfEmpty(Mono.empty());
    }
}
