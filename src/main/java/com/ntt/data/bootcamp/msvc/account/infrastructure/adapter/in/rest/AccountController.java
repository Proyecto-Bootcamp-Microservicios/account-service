package com.ntt.data.bootcamp.msvc.account.infrastructure.adapter.in.rest;

import com.ntt.data.bootcamp.msvc.account.application.dto.command.CreateAccountCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.command.TransactionExecutionCommand;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.AccountResponse;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.BalanceResponse;
import com.ntt.data.bootcamp.msvc.account.application.dto.response.TransactionExecuteResponse;
import com.ntt.data.bootcamp.msvc.account.application.port.in.*;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

  private final ICreateCheckingAccountUseCase createCheckingAccountUseCase;
  private final ICreateSavingAccountUseCase createSavingAccountUseCase;
  private final ICreateFixedTermAccountUseCase createFixedTermAccountUseCase;
  private final IExecuteTransactionUseCase executeTransactionUseCase;
  private final IChangeStatusUseCase changeStatusUseCase;
  private final IRetriveAccountBalanceUseCase retriveAccountBalanceUseCase;
  private final IRetrieveAccountUseCase retrieveAccountUseCase;

  @GetMapping
  public Mono<ResponseEntity<Flux<AccountResponse>>> retrieveAllAccounts() {
    return Mono.just(new ResponseEntity<>(retrieveAccountUseCase.findAll(), HttpStatus.OK));
  }

  @GetMapping("/type/{type}")
  public Mono<ResponseEntity<Flux<AccountResponse>>> findAllByType(@PathVariable AccountType type) {
    return Mono.just(ResponseEntity.ok(retrieveAccountUseCase.findAllByType(type)));
  }

  @GetMapping("/status/{status}")
  public Mono<ResponseEntity<Flux<AccountResponse>>> findAllByStatus(@PathVariable AccountStatus status) {
    return Mono.just(ResponseEntity.ok(retrieveAccountUseCase.findAllByStatus(status)));
  }

  @GetMapping("/customer/{customerId}")
  public Mono<ResponseEntity<Flux<AccountResponse>>> findAllByCustomer(@PathVariable String customerId) {
    return retrieveAccountUseCase.findAllByCustomer(customerId)
        .hasElements()
        .map(hasElements ->
            hasElements
                ? ResponseEntity.ok(retrieveAccountUseCase.findAllByCustomer(customerId))
                : ResponseEntity.notFound().build()
        );
  }

  @GetMapping("/type/{type}/status/{status}")
  public Mono<ResponseEntity<Flux<AccountResponse>>> findByTypeAndStatus(
      @PathVariable AccountType type, @PathVariable AccountStatus status) {
    return Mono.just(ResponseEntity.ok(retrieveAccountUseCase.findAllByTypeAndStatus(type, status)));
  }

  @GetMapping("/{id}/balance")
  public Mono<ResponseEntity<BalanceResponse>> retriveAccountBalance(@PathVariable String id) {
    return retriveAccountBalanceUseCase
        .retriveAccountBalance(id)
        .map(balanceResponse -> new ResponseEntity<>(balanceResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PostMapping("/checking")
  public Mono<ResponseEntity<AccountResponse>> createCheckingAccount(
      @RequestBody CreateAccountCommand request) {
    return createCheckingAccountUseCase
        .createCheckingAccountWithAmountZero(request)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PostMapping("/saving")
  public Mono<ResponseEntity<AccountResponse>> createSavingAccount(
      @RequestBody CreateAccountCommand request) {
    return createSavingAccountUseCase
        .createSavingAccount(request)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PostMapping("/fixed-term")
  public Mono<ResponseEntity<AccountResponse>> createFixedTermAccount(
      @RequestBody CreateAccountCommand request) {
    return createFixedTermAccountUseCase
        .createFixedTermAccount(request)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PutMapping("/{accountNumber}")
  public Mono<ResponseEntity<TransactionExecuteResponse>> executeTransaction(
      @PathVariable(value = "accountNumber") String id, @RequestBody TransactionExecutionCommand request) {
    return executeTransactionUseCase
        .executeTransaction(id, request)
        .map(
            transactionExecuteResponse ->
                new ResponseEntity<>(transactionExecuteResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PutMapping("/{id}/status/{status}")
  public Mono<ResponseEntity<AccountResponse>> changeStatus(
      @PathVariable String id, @PathVariable String status) {
    return changeStatusUseCase
        .changeStatus(id, AccountStatus.valueOf(status))
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<AccountResponse>> deleteAccount(@PathVariable String id) {
    return changeStatusUseCase
        .changeStatus(id, AccountStatus.CLOSED)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

}
