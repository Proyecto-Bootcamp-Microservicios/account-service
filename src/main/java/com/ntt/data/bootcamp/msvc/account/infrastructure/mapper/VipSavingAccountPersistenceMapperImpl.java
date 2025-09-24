package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

import com.ntt.data.bootcamp.msvc.account.domain.VipSavingAccount;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AccountHolder;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Audit;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import com.ntt.data.bootcamp.msvc.account.domain.vo.TransactionLimit;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.VipSavingAccountCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class VipSavingAccountPersistenceMapperImpl implements IVipSavingAccountPersistenceMapper {

  @Override
  public VipSavingAccountCollection toEntity(VipSavingAccount vipSavingAccount) {
    BalanceCollection balanceCollection = new BalanceCollection();
    balanceCollection.setAmount(vipSavingAccount.getAmount());
    balanceCollection.setCurrencyCode(vipSavingAccount.getCurrencyCode());
    balanceCollection.setTimestamp(vipSavingAccount.getBalance().getTimestamp());

    List<AccountHolderCollection> holderCollections = vipSavingAccount.getHolders().stream()
        .map(this::mapAccountHolder)
        .collect(Collectors.toList());

    // Transaction Limit
    TransactionLimitCollection transactionLimitCollection = new TransactionLimitCollection();
    transactionLimitCollection.setMaxFreeTransactions(vipSavingAccount.getTransactionLimit().getMaxFreeTransactions());
    transactionLimitCollection.setFixedCommissions(vipSavingAccount.getTransactionLimit().getFixedCommissions());
    transactionLimitCollection.setPercentageCommissions(vipSavingAccount.getTransactionLimit().getPercentageCommissions());
    transactionLimitCollection.setCurrentTransactions(vipSavingAccount.getTransactionLimit().getCurrentTransactions());
    transactionLimitCollection.setMonthStartDate(vipSavingAccount.getTransactionLimit().getMonthStartDate());

    VipSavingAccountCollection collection = new VipSavingAccountCollection(
        vipSavingAccount.getIdValue(),
        vipSavingAccount.getCustomerId(),
        vipSavingAccount.getCustomerType(),
        vipSavingAccount.getAccountType().name(),
        vipSavingAccount.getAccountNumber(),
        vipSavingAccount.getExternalAccountNumber(),
        vipSavingAccount.getStatus().name(),
        balanceCollection,
        holderCollections,
        vipSavingAccount.getCreatedAt(),
        vipSavingAccount.getUpdatedAt(),
        transactionLimitCollection,
        vipSavingAccount.getMinimumDailyAverage()
    );



    return collection;
  }

  @Override
  public VipSavingAccount toDomain(VipSavingAccountCollection vipSavingAccountCollection) {
    // Balance
    Balance balance = Balance.reconstruct(
        vipSavingAccountCollection.getBalance().getCurrencyCode(),
        vipSavingAccountCollection.getBalance().getAmount(),
        vipSavingAccountCollection.getBalance().getTimestamp());

    // Holders
    Set<AccountHolder> holders = vipSavingAccountCollection.getHolders().stream()
        .map(this::mapAccountHolderFromCollection)
        .collect(Collectors.toSet());

    // Audit
    Audit audit = Audit.reconstruct(
        vipSavingAccountCollection.getCreatedAt(),
        vipSavingAccountCollection.getUpdatedAt());

    // Transaction Limit
    TransactionLimit transactionLimit = TransactionLimit.reconstruct(
        vipSavingAccountCollection.getTransactionLimit().getMaxFreeTransactions(),
        vipSavingAccountCollection.getTransactionLimit().getFixedCommissions(),
        vipSavingAccountCollection.getTransactionLimit().getPercentageCommissions(),
        vipSavingAccountCollection.getTransactionLimit().getCurrentTransactions(),
        vipSavingAccountCollection.getTransactionLimit().getMonthStartDate());

    return VipSavingAccount.reconstruct(
        vipSavingAccountCollection.getId(),
        vipSavingAccountCollection.getCustomerId(),
        vipSavingAccountCollection.getCustomerType(),
        holders.iterator().next().getDocumentType(), // Primary holder
        holders.iterator().next().getDocumentNumber(), // Primary holder
        vipSavingAccountCollection.getAccountNumber(),
        vipSavingAccountCollection.getExternalAccountNumber(),
        AccountType.valueOf(vipSavingAccountCollection.getAccountType()),
        AccountStatus.valueOf(vipSavingAccountCollection.getStatus()),
        balance,
        audit,
        holders,
        transactionLimit,
        vipSavingAccountCollection.getMinimumDailyAverage());
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
}