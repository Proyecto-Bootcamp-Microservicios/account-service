package com.ntt.data.bootcamp.msvc.account.application.dto.command;

import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationDirection;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Command to execute a transaction on an account.
 * Example JSON:
 * {"operationType":"DEBIT","operationDirection":"IN","amount":100,
 *  "description":"test","transactionId":"1","destinationAccountId":"1",
 *  "customerDocumentType":"DNI","customerDocumentNumber":"12345678"}
 */

@Getter
@AllArgsConstructor
public class TransactionExecutionCommand {
  private OperationType operationType;
  private OperationDirection operationDirection;
  private BigDecimal amount;
  private String description;
  private String transactionId;
  private String destinationAccountId;
  private String customerDocumentType;
  private String customerDocumentNumber;
}
