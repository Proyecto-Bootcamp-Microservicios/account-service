package com.NTTDATA.bootcamp.msvc_account.infrastructure.config;

import com.NTTDATA.bootcamp.msvc_account.application.port.in.IChangeStatusUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateCheckingAccountUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateSavingAccountUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.IExecuteTransactionUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.out.IRetriveCustomerByIdPort;
import com.NTTDATA.bootcamp.msvc_account.application.usecase.ChangeStatusServiceImpl;
import com.NTTDATA.bootcamp.msvc_account.application.usecase.CreateCheckingAccountServiceImpl;
import com.NTTDATA.bootcamp.msvc_account.application.usecase.CreateSavingAccountServiceImpl;
import com.NTTDATA.bootcamp.msvc_account.application.usecase.ExecuteTransactionServiceImpl;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.IAccountRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    ICreateCheckingAccountUseCase createCheckingAccountUseCase(IRetriveCustomerByIdPort retriveCustomerByIdPort,
                                                               IAccountRepositoryPort accountRepositoryPort){
        return new CreateCheckingAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort);
    }

    @Bean
    ICreateSavingAccountUseCase createSavingAccountUseCase(IRetriveCustomerByIdPort retriveCustomerByIdPort,
                                                           IAccountRepositoryPort accountRepositoryPort){
        return new CreateSavingAccountServiceImpl(retriveCustomerByIdPort, accountRepositoryPort);
    }

    @Bean
    IExecuteTransactionUseCase executeTransactionUseCase(IAccountRepositoryPort accountRepositoryPort
                                                         ){
        return new ExecuteTransactionServiceImpl(accountRepositoryPort);
    }

    @Bean
    IChangeStatusUseCase changeStatusUseCase(IAccountRepositoryPort accountRepositoryPort) {
        return new ChangeStatusServiceImpl(accountRepositoryPort);
    }

}
