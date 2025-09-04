package com.NTTDATA.bootcamp.msvc_account.infrastructure.adapter.in.rest;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountCommand;
import com.NTTDATA.bootcamp.msvc_account.application.dto.command.TransactionExecutionCommand;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.TransactionExecuteResponse;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.IChangeStatusUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateCheckingAccountUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateSavingAccountUseCase;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.IExecuteTransactionUseCase;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

    private final ICreateCheckingAccountUseCase createCheckingAccountUseCase;
    private final ICreateSavingAccountUseCase createSavingAccountUseCase;
    private final IExecuteTransactionUseCase executeTransactionUseCase;
    private final IChangeStatusUseCase changeStatusUseCase;

    @PostMapping("/checking")
    public Mono<ResponseEntity<AccountResponse>> createCheckingAccount(@RequestBody CreateAccountCommand request){
        return createCheckingAccountUseCase.createCheckingAccountWithAmountZero(request)
                .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PostMapping("/saving")
    public Mono<ResponseEntity<AccountResponse>> createSavingAccount(@RequestBody CreateAccountCommand request){
        return createSavingAccountUseCase.createSavingAccount(request)
                .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TransactionExecuteResponse>> executeTransaction(@PathVariable String id, @RequestBody TransactionExecutionCommand request){
        return executeTransactionUseCase.executeTransaction(id, request)
                .map(transactionExecuteResponse -> new ResponseEntity<>(transactionExecuteResponse, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}/status/{status}")
    public Mono<ResponseEntity<AccountResponse>> changeStatus(@PathVariable String id, @PathVariable String status){
        return changeStatusUseCase.changeStatus(id, AccountStatus.valueOf(status))
                .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

}
