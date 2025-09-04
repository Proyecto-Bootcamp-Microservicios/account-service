package com.NTTDATA.bootcamp.msvc_account.domain.port.out;

import com.NTTDATA.bootcamp.msvc_account.domain.CheckingAccount;
import reactor.core.publisher.Mono;

public interface ICheckingAccountRepositoryPort extends IGenericRepositoryPort<CheckingAccount, String> {
}
