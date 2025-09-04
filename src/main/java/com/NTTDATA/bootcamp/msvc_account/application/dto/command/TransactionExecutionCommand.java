package com.NTTDATA.bootcamp.msvc_account.application.dto.command;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/*to json example: {
    "operationType": "DEBIT",
    "amount": 100,
    "description": "test",
    "transactionId": "1",
    "destinationAccountId": "1",
    "customerDocumentType": "DNI",
    "customerDocumentNumber": "12345678"
}*/

@Getter
@AllArgsConstructor
public class TransactionExecutionCommand {
    private OperationType operationType;
    private BigDecimal amount;
    private String description;
    private String transactionId;
    private String destinationAccountId;
    private String customerDocumentType;
    private String customerDocumentNumber;
}
