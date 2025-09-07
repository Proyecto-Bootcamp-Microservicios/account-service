package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

import com.ntt.data.bootcamp.msvc.account.domain.SavingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.vo.*;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AccountHolder;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Audit;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import com.ntt.data.bootcamp.msvc.account.domain.vo.TransactionLimit;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.SavingAccountCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SavingAccountPersistenceMapperImpl implements ISavingAccountPersistenceMapper {

  @Override
  public SavingAccountCollection toEntity(SavingAccount savingAccount) {

    BalanceCollection balanceCollection =
        new BalanceCollection(
            savingAccount.getBalance().getAmount(),
            savingAccount.getBalance().getCurrency().getCurrencyCode(),
            savingAccount.getBalance().getTimestamp());
    List<AccountHolderCollection> accountHolderCollections =
        savingAccount.getHolders().stream()
            .map(
                accountHolder ->
                    new AccountHolderCollection(
                        accountHolder.getDocumentType(),
                        accountHolder.getDocumentNumber(),
                        accountHolder.getParticipationPercentage(),
                        accountHolder.isPrimaryHolder()))
            .collect(Collectors.toList());

    TransactionLimit transactionLimit = savingAccount.getTransactionLimit();
    TransactionLimitCollection transactionLimitCollection =
        new TransactionLimitCollection(
            transactionLimit.getMaxFreeTransactions(),
            transactionLimit.getFixedCommissions(),
            transactionLimit.getPercentageCommissions(),
            transactionLimit.getCurrentTransactions(),
            transactionLimit.getMonthStartDate());

    return new SavingAccountCollection(
        savingAccount.getIdValue(),
        savingAccount.getCustomerId(),
        savingAccount.getCustomerType(),
        savingAccount.getAccountType().name(),
        savingAccount.getAccountNumber(),
        savingAccount.getExternalAccountNumber(),
        savingAccount.getStatus().name(),
        balanceCollection,
        accountHolderCollections,
        savingAccount.getCreatedAt(),
        savingAccount.getUpdatedAt(),
        transactionLimitCollection);
  }

  @Override
  public SavingAccount toDomain(SavingAccountCollection savingAccountCollection) {
    AccountHolderCollection accountHolderCollection =
        savingAccountCollection.getHolders().stream()
            .filter(AccountHolderCollection::isPrimary)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Primary holder not found"));
    BalanceCollection balanceCollection = savingAccountCollection.getBalance();
    Balance balance =
        Balance.reconstruct(
            balanceCollection.getCurrencyCode(),
            balanceCollection.getAmount(),
            balanceCollection.getTimestamp());
    Set<AccountHolder> accountHolders =
        savingAccountCollection.getHolders().stream()
            .map(
                accountHolderCollect -> {
                  if (accountHolderCollect.isPrimary()) {
                    return AccountHolder.ofPrimaryHolder(
                        accountHolderCollect.getDocumentType(),
                        accountHolderCollect.getDocumentNumber(),
                        accountHolderCollect.getParticipationPercentage());
                  } else {
                    return AccountHolder.ofSecondaryHolder(
                        accountHolderCollect.getDocumentType(),
                        accountHolderCollect.getDocumentNumber(),
                        accountHolderCollect.getParticipationPercentage());
                  }
                })
            .collect(Collectors.toSet());
    TransactionLimit transactionLimit =
        TransactionLimit.reconstruct(
            savingAccountCollection.getTransactionLimit().getMaxFreeTransactions(),
            savingAccountCollection.getTransactionLimit().getFixedCommissions(),
            savingAccountCollection.getTransactionLimit().getPercentageCommissions(),
            savingAccountCollection.getTransactionLimit().getCurrentTransactions(),
            savingAccountCollection.getTransactionLimit().getMonthStartDate());

    return SavingAccount.reconstruct(
        savingAccountCollection.getId(),
        savingAccountCollection.getCustomerId(),
        savingAccountCollection.getCustomerType(),
        accountHolderCollection.getDocumentType(),
        accountHolderCollection.getDocumentNumber(),
        savingAccountCollection.getAccountNumber(),
        savingAccountCollection.getExternalAccountNumber(),
        AccountType.valueOf(savingAccountCollection.getAccountType()),
        AccountStatus.valueOf(savingAccountCollection.getStatus()),
        balance,
        Audit.reconstruct(
            savingAccountCollection.getCreatedAt(), savingAccountCollection.getUpdatedAt()),
        accountHolders,
        transactionLimit);
  }
}
