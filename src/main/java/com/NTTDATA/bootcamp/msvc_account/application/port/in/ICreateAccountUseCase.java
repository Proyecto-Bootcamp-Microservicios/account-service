package com.NTTDATA.bootcamp.msvc_account.application.port.in;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountRequest;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;

public interface ICreateAccountUseCase {
    AccountResponse createAccount(CreateAccountRequest request);
}
