package com.ntt.data.bootcamp.msvc.account.infrastructure.config;

import com.ntt.data.bootcamp.msvc.account.application.port.in.*;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IRetriveCustomerByIdPort;
import com.ntt.data.bootcamp.msvc.account.application.port.out.IValidateDebtPort;
import com.ntt.data.bootcamp.msvc.account.application.usecase.*;
import com.ntt.data.bootcamp.msvc.account.domain.port.out.IAccountRepositoryPort;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.repository.ISpringDailyBalanceSnapshotRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that wires application use case beans with their dependencies.
 */
@Configuration
public class UseCaseConfig {

  @Bean
  ICreateCheckingAccountUseCase createCheckingAccountUseCase(
      IRetriveCustomerByIdPort retriveCustomerByIdPort,
      IAccountRepositoryPort accountRepositoryPort,
      IValidateDebtPort validateDebtPort) {
    return new CreateCheckingAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort, validateDebtPort);
  }

  @Bean
  ICreateSavingAccountUseCase createSavingAccountUseCase(
      IRetriveCustomerByIdPort retriveCustomerByIdPort,
      IAccountRepositoryPort accountRepositoryPort,
      IValidateDebtPort validateDebtPort) {
    return new CreateSavingAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort, validateDebtPort);
  }

  @Bean
  ICreateFixedTermAccountUseCase createFixedTermAccountUseCase(
      IRetriveCustomerByIdPort retriveCustomerByIdPort,
      IAccountRepositoryPort accountRepositoryPort,
      IValidateDebtPort validateDebtPort) {
    return new CreateFixedTermAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort, validateDebtPort);
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

  @Bean
  IRetrieveAccountUseCase retrieveAccountUseCase(IAccountRepositoryPort accountRepositoryPort) {
    return new RetrieveAccountServiceImpl(accountRepositoryPort);
  }

  // NUEVOS CASOS DE USO
  @Bean
  ICreateVipSavingAccountUseCase createVipSavingAccountUseCase(
      IRetriveCustomerByIdPort retriveCustomerByIdPort,
      IAccountRepositoryPort accountRepositoryPort,
      IValidateDebtPort validateDebtPort) {
    return new CreateVipSavingAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort, validateDebtPort);
  }

  @Bean
  ICreatePymeCheckingAccountUseCase createPymeCheckingAccountUseCase(
      IRetriveCustomerByIdPort retriveCustomerByIdPort,
      IAccountRepositoryPort accountRepositoryPort,
      IValidateDebtPort validateDebtPort) {
    return new CreatePymeCheckingAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort, validateDebtPort);
  }

  @Bean
  public IGenerateCustomerBalanceReportUseCase customerBalanceReportUseCase(
      ISpringDailyBalanceSnapshotRepository dailyBalanceRepository) {
    return new CustomerBalanceReportServiceImpl(dailyBalanceRepository);
  }
}
