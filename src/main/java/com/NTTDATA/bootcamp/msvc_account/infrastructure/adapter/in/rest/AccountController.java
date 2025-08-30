package com.NTTDATA.bootcamp.msvc_account.infrastructure.adapter.in.rest;

import com.NTTDATA.bootcamp.msvc_account.application.dto.command.CreateAccountRequest;
import com.NTTDATA.bootcamp.msvc_account.application.dto.response.AccountResponse;
import com.NTTDATA.bootcamp.msvc_account.application.port.in.ICreateSavingAccountUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

    private final ICreateSavingAccountUseCase createSavingAccountUseCase;

    @PostMapping
    public Mono<ResponseEntity<AccountResponse>> createAccount(@RequestBody CreateAccountRequest request){
        return createSavingAccountUseCase.execute(request)
                .map(accountResponse -> new ResponseEntity<>(accountResponse, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

}
