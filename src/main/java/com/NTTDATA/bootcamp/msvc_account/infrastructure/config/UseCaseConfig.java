package com.NTTDATA.bootcamp.msvc_account.infrastructure.config;

import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateSavingAccountUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.usecase.CreateSavingAccountServiceImpl;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.ISavingAccountRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    ICreateSavingAccountUseCase createSavingAccountUseCase(ISavingAccountRepositoryPort savingAccountRepositoryPort){
        return new CreateSavingAccountServiceImpl(savingAccountRepositoryPort);
    }

}
