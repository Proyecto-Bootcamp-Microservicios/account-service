package com.ntt.data.bootcamp.msvc.account.infrastructure.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Spring configuration providing a load-balanced {@link WebClient.Builder}.
 */
@Configuration
public class WebClientConfig {

  /** Creates a load-balanced WebClient builder for inter-service communication. */
  @Bean
  @LoadBalanced
  WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}
