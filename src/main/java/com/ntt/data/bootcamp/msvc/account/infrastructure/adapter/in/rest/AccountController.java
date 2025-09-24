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
/**
 * REST controller exposing reactive endpoints to manage bank accounts.
 * <p>
 * It delegates operations to application layer use cases and returns Reactor types
 * for non-blocking I/O with Spring WebFlux.
 */
public class AccountController {

  private final ICreateCheckingAccountUseCase createCheckingAccountUseCase;
  private final ICreateSavingAccountUseCase createSavingAccountUseCase;
  private final ICreateFixedTermAccountUseCase createFixedTermAccountUseCase;
  private final IExecuteTransactionUseCase executeTransactionUseCase;
  private final IChangeStatusUseCase changeStatusUseCase;
  private final IRetriveAccountBalanceUseCase retriveAccountBalanceUseCase;
  private final IRetrieveAccountUseCase retrieveAccountUseCase;
  private final ICreateVipSavingAccountUseCase createVipSavingAccountUseCase;
  private final ICreatePymeCheckingAccountUseCase createPymeCheckingAccountUseCase;

  /**
   * Retrieves an account by its identifier.
   *
   * @param id account identifier
   * @return Mono emitting 200 with {@link AccountResponse} or 404 when not found
   */
  @GetMapping("/{id}")
  public Mono<ResponseEntity<AccountResponse>> retrieveAccountById(@PathVariable String id) {
    return retrieveAccountUseCase.findById(id)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Retrieves all accounts without filters.
   *
   * @return Mono emitting {@link ResponseEntity} with a {@link Flux} of {@link AccountResponse}
   */
  @GetMapping
  public Mono<ResponseEntity<Flux<AccountResponse>>> retrieveAllAccounts() {
    return Mono.just(new ResponseEntity<>(retrieveAccountUseCase.findAll(), HttpStatus.OK));
  }

  /**
   * Retrieves all accounts filtered by type.
   *
   * @param type account type to filter
   * @return Mono emitting {@link ResponseEntity} with a {@link Flux} of {@link AccountResponse}
   */
  @GetMapping("/type/{type}")
  public Mono<ResponseEntity<Flux<AccountResponse>>> findAllByType(@PathVariable AccountType type) {
    return Mono.just(ResponseEntity.ok(retrieveAccountUseCase.findAllByType(type)));
  }

  /**
   * Retrieves all accounts filtered by status.
   *
   * @param status account status to filter
   * @return Mono emitting {@link ResponseEntity} with a {@link Flux} of {@link AccountResponse}
   */
  @GetMapping("/status/{status}")
  public Mono<ResponseEntity<Flux<AccountResponse>>> findAllByStatus(@PathVariable AccountStatus status) {
    return Mono.just(ResponseEntity.ok(retrieveAccountUseCase.findAllByStatus(status)));
  }

  /**
   * Retrieves all accounts belonging to a specific customer.
   * Returns 404 when the customer has no accounts.
   *
   * @param customerId customer identifier
   * @return Mono emitting 200 with accounts or 404 when none found
   */
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

  /**
   * Retrieves accounts filtered by both type and status.
   *
   * @param type account type
   * @param status account status
   * @return Mono emitting {@link ResponseEntity} with a {@link Flux} of {@link AccountResponse}
   */
  @GetMapping("/type/{type}/status/{status}")
  public Mono<ResponseEntity<Flux<AccountResponse>>> findByTypeAndStatus(
      @PathVariable AccountType type, @PathVariable AccountStatus status) {
    return Mono.just(ResponseEntity.ok(retrieveAccountUseCase.findAllByTypeAndStatus(type, status)));
  }

  /**
   * Retrieves the current balance for the given account.
   *
   * @param id internal account identifier
   * @return Mono emitting 200 with {@link BalanceResponse} or 400 when not found/invalid
   */
  @GetMapping("/{id}/balance")
  public Mono<ResponseEntity<BalanceResponse>> retriveAccountBalance(@PathVariable String id) {
    return retriveAccountBalanceUseCase
        .retriveAccountBalance(id)
        .map(balanceResponse -> new ResponseEntity<>(balanceResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a checking account with zero initial amount.
   *
   * @param request account creation command
   * @return Mono emitting 201 with created account or 400 on validation errors
   */
  @PostMapping("/checking")
  public Mono<ResponseEntity<AccountResponse>> createCheckingAccount(
      @RequestBody CreateAccountCommand request) {
    return createCheckingAccountUseCase
        .createCheckingAccountWithAmountZero(request)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a saving account.
   *
   * @param request account creation command
   * @return Mono emitting 201 with created account or 400 on validation errors
   */
  @PostMapping("/saving")
  public Mono<ResponseEntity<AccountResponse>> createSavingAccount(
      @RequestBody CreateAccountCommand request) {
    return createSavingAccountUseCase
        .createSavingAccount(request)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a fixed-term account.
   *
   * @param request account creation command
   * @return Mono emitting 201 with created account or 400 on validation errors
   */
  @PostMapping("/fixed-term")
  public Mono<ResponseEntity<AccountResponse>> createFixedTermAccount(
      @RequestBody CreateAccountCommand request) {
    return createFixedTermAccountUseCase
        .createFixedTermAccount(request)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  // Agregar estos métodos al controlador existente:

  @PostMapping("/vip-saving")
  public Mono<AccountResponse> createVipSavingAccount(@RequestBody CreateAccountCommand command) {
    return createVipSavingAccountUseCase.createVipSavingAccount(command);
  }

  @PostMapping("/pyme-checking")
  public Mono<AccountResponse> createPymeCheckingAccount(@RequestBody CreateAccountCommand command) {
    return createPymeCheckingAccountUseCase.createPymeCheckingAccount(command);
  }

  /**
   * Executes a transaction on an account number (deposit, withdrawal, transfer, payment).
   *
   * @param id account number
   * @param request transaction command
   * @return Mono emitting 200 with execution result or 400 on errors
   */
  @PostMapping("/{accountNumber}")
  public Mono<ResponseEntity<TransactionExecuteResponse>> executeTransaction(
      @PathVariable(value = "accountNumber") String id, @RequestBody TransactionExecutionCommand request) {
    return executeTransactionUseCase
        .executeTransaction(id, request)
        .map(
            transactionExecuteResponse ->
                new ResponseEntity<>(transactionExecuteResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  /**
   * Changes the status of an account.
   *
   * @param id account identifier
   * @param status new status name (must match {@link AccountStatus})
   * @return Mono emitting 200 with updated account or 400 on errors
   */
  @PutMapping("/{id}/status/{status}")
  public Mono<ResponseEntity<AccountResponse>> changeStatus(
      @PathVariable String id, @PathVariable String status) {
    return changeStatusUseCase
        .changeStatus(id, AccountStatus.valueOf(status))
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  /**
   * Soft-deletes an account by changing its status to CLOSED.
   *
   * @param id account identifier
   * @return Mono emitting 200 with updated account or 400 on errors
   */
  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<AccountResponse>> deleteAccount(@PathVariable String id) {
    return changeStatusUseCase
        .changeStatus(id, AccountStatus.CLOSED)
        .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

}
