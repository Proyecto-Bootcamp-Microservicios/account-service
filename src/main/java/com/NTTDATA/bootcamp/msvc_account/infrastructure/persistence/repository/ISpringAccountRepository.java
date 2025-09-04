package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.repository;

import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.AccountCollection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISpringAccountRepository extends ISpringGenericRepository<AccountCollection, String> {
    Mono<Long> countByCustomerIdAndAccountType(String customerId, String accountType);
    Flux<AccountCollection> findByAccountType(String accountType);
}
