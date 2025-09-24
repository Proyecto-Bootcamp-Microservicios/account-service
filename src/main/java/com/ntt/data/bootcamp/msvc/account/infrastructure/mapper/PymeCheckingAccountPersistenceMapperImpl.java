package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

import com.ntt.data.bootcamp.msvc.account.domain.PymeCheckingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AccountHolder;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Audit;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AuthorizedSigner;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import com.ntt.data.bootcamp.msvc.account.domain.vo.TransactionLimit;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.PymeCheckingAccountCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AuthorizedSignerCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PymeCheckingAccountPersistenceMapperImpl implements IPymeCheckingAccountPersistenceMapper {

  @Override
  public PymeCheckingAccountCollection toEntity(PymeCheckingAccount pymeCheckingAccount) {

    // Balance
    BalanceCollection balanceCollection = new BalanceCollection();
    balanceCollection.setAmount(pymeCheckingAccount.getAmount());
    balanceCollection.setCurrencyCode(pymeCheckingAccount.getCurrencyCode());
    balanceCollection.setTimestamp(pymeCheckingAccount.getBalance().getTimestamp());

    // Holders
    Set<AccountHolderCollection> holderCollections = pymeCheckingAccount.getHolders().stream()
        .map(this::mapAccountHolder)
        .collect(Collectors.toSet());

    // Transaction Limit
    TransactionLimitCollection transactionLimitCollection = new TransactionLimitCollection();
    transactionLimitCollection.setMaxFreeTransactions(pymeCheckingAccount.getTransactionLimit().getMaxFreeTransactions());
    transactionLimitCollection.setFixedCommissions(pymeCheckingAccount.getTransactionLimit().getFixedCommissions());
    transactionLimitCollection.setPercentageCommissions(pymeCheckingAccount.getTransactionLimit().getPercentageCommissions());
    transactionLimitCollection.setCurrentTransactions(pymeCheckingAccount.getTransactionLimit().getCurrentTransactions());
    transactionLimitCollection.setMonthStartDate(pymeCheckingAccount.getTransactionLimit().getMonthStartDate());


    // Signers
    Set<AuthorizedSignerCollection> signerCollections = pymeCheckingAccount.getSigners().stream()
        .map(this::mapAuthorizedSigner)
        .collect(Collectors.toSet());

    PymeCheckingAccountCollection collection = new PymeCheckingAccountCollection(
        pymeCheckingAccount.getIdValue(),
        pymeCheckingAccount.getCustomerId(),
        pymeCheckingAccount.getCustomerType(),
        pymeCheckingAccount.getAccountType().name(),
        pymeCheckingAccount.getAccountNumber(),
        pymeCheckingAccount.getExternalAccountNumber(),
        pymeCheckingAccount.getStatus().name(),
        holderCollections.stream().collect(Collectors.toList()),
        balanceCollection,
        pymeCheckingAccount.getCreatedAt(),
        pymeCheckingAccount.getUpdatedAt(),
        pymeCheckingAccount.getMaintenanceFee(),
        pymeCheckingAccount.getNextFeeDate(),
        signerCollections.stream().collect(Collectors.toList()),
        transactionLimitCollection
    );

    return collection;
  }

  @Override
  public PymeCheckingAccount toDomain(PymeCheckingAccountCollection pymeCheckingAccountCollection) {
    // Balance
    Balance balance = Balance.reconstruct(
        pymeCheckingAccountCollection.getBalance().getCurrencyCode(),
        pymeCheckingAccountCollection.getBalance().getAmount(),
        pymeCheckingAccountCollection.getBalance().getTimestamp());

    // Holders
    Set<AccountHolder> holders = pymeCheckingAccountCollection.getHolders().stream()
        .map(this::mapAccountHolderFromCollection)
        .collect(Collectors.toSet());

    // Audit
    Audit audit = Audit.reconstruct(
        pymeCheckingAccountCollection.getCreatedAt(),
        pymeCheckingAccountCollection.getUpdatedAt());

    // Transaction Limit
    TransactionLimit transactionLimit = TransactionLimit.reconstruct(
        pymeCheckingAccountCollection.getTransactionLimit().getMaxFreeTransactions(),
        pymeCheckingAccountCollection.getTransactionLimit().getFixedCommissions(),
        pymeCheckingAccountCollection.getTransactionLimit().getPercentageCommissions(),
        pymeCheckingAccountCollection.getTransactionLimit().getCurrentTransactions(),
        pymeCheckingAccountCollection.getTransactionLimit().getMonthStartDate());

    // Signers
    Set<AuthorizedSigner> signers = pymeCheckingAccountCollection.getAuthorizedSigners().stream()
        .map(this::mapAuthorizedSignerFromCollection)
        .collect(Collectors.toSet());

    return PymeCheckingAccount.reconstruct(
        pymeCheckingAccountCollection.getId(),
        pymeCheckingAccountCollection.getCustomerId(),
        pymeCheckingAccountCollection.getCustomerType(),
        holders.iterator().next().getDocumentType(), // Primary holder
        holders.iterator().next().getDocumentNumber(), // Primary holder
        pymeCheckingAccountCollection.getAccountNumber(),
        pymeCheckingAccountCollection.getExternalAccountNumber(),
        AccountType.valueOf(pymeCheckingAccountCollection.getAccountType()),
        AccountStatus.valueOf(pymeCheckingAccountCollection.getStatus()),
        balance,
        audit,
        holders,
        pymeCheckingAccountCollection.getMaintenanceFee(),
        pymeCheckingAccountCollection.getNextFeeDate(),
        signers,
        transactionLimit);
  }

  private AccountHolderCollection mapAccountHolder(AccountHolder accountHolder) {
    AccountHolderCollection collection = new AccountHolderCollection();
    collection.setDocumentType(accountHolder.getDocumentType());
    collection.setDocumentNumber(accountHolder.getDocumentNumber());
    collection.setParticipationPercentage(accountHolder.getParticipationPercentage());
    collection.setPrimary(accountHolder.isPrimaryHolder());
    return collection;
  }

  private AccountHolder mapAccountHolderFromCollection(AccountHolderCollection collection) {
    if (collection.isPrimary()) {
      return AccountHolder.ofPrimaryHolder(
          collection.getDocumentType(),
          collection.getDocumentNumber(),
          collection.getParticipationPercentage());
    } else {
      return AccountHolder.ofSecondaryHolder(
          collection.getDocumentType(),
          collection.getDocumentNumber(),
          collection.getParticipationPercentage());
    }
  }

  private AuthorizedSignerCollection mapAuthorizedSigner(AuthorizedSigner authorizedSigner) {
    AuthorizedSignerCollection collection = new AuthorizedSignerCollection();
    collection.setDocumentType(authorizedSigner.getDocumentType());
    collection.setDocumentNumber(authorizedSigner.getDocumentNumber());
    return collection;
  }

  private AuthorizedSigner mapAuthorizedSignerFromCollection(AuthorizedSignerCollection collection) {
    return AuthorizedSigner.of(
        collection.getDocumentType(),
        collection.getDocumentNumber());
  }
}