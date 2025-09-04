package com.NTTDATA.bootcamp.msvc_account.infrastructure.scheduler;

import com.NTTDATA.bootcamp.msvc_account.domain.Account;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.port.out.IAccountRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class FixedTermAccountUpdater {

    private final IAccountRepositoryPort accountRepositoryPort;

    @Scheduled(cron = "0 0 1 * * ?")
    public void updateFixedTermAccounts() {
        accountRepositoryPort.findByAccountType(AccountType.FIXED_TERM.name())
                .flatMap(account -> {
                        Account updatedAccount = account.updateOperationDateIfNeeded();
                        if (updatedAccount != account) {
                            log.info("Actualizando cuenta {} con nueva fecha de operación",
                                    updatedAccount.getAccountNumber());
                            return accountRepositoryPort.save(updatedAccount);
                        }
                    return Mono.just(account);
                })
                .doOnComplete(() ->
                        log.info("Actualización de fechas de operación completada"))
                .doOnError(error ->
                        log.error("Error actualizando fechas de operación: {}", error.getMessage()))
                .subscribe();
    }

}
