package com.ntt.data.bootcamp.msvc.account.infrastructure.config;

import com.ntt.data.bootcamp.msvc.account.application.port.in.*;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IRetriveCustomerByIdPort;
import com.ntt.data.bootcamp.msvc.account.application.usecase.*;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  ICreateCheckingAccountUseCase createCheckingAccountUseCase(
      IRetriveCustomerByIdPort retriveCustomerByIdPort,
      IAccountRepositoryPort accountRepositoryPort) {
    return new CreateCheckingAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort);
  }

  @Bean
  ICreateSavingAccountUseCase createSavingAccountUseCase(
      IRetriveCustomerByIdPort retriveCustomerByIdPort,
      IAccountRepositoryPort accountRepositoryPort) {
    return new CreateSavingAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort);
  }

  @Bean
  ICreateFixedTermAccountUseCase createFixedTermAccountUseCase(
      IRetriveCustomerByIdPort retriveCustomerByIdPort,
      IAccountRepositoryPort accountRepositoryPort) {
    return new CreateFixedTermAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort);
  }

  @Bean
  IExecuteTransactionUseCase executeTransactionUseCase(
      IAccountRepositoryPort accountRepositoryPort) {
    return new ExecuteTransactionServiceImpl(accountRepositoryPort);
  }

  @Bean
  IChangeStatusUseCase changeStatusUseCase(IAccountRepositoryPort accountRepositoryPort) {
    return new ChangeStatusServiceImpl(accountRepositoryPort);
  }

  @Bean
  IRetriveAccountBalanceUseCase retriveAccountBalanceUseCase(
      IAccountRepositoryPort accountRepositoryPort) {
    return new RetriveAccountBalanceServiceImpl(accountRepositoryPort);
  }
}
