package com.NTTDATA.bootcamp.msvc_account.application.usecase;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountRequest;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateAccountUseCase;

public class CreateAccountServiceImpl implements ICreateAccountUseCase {
    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        return null;
    }
}
