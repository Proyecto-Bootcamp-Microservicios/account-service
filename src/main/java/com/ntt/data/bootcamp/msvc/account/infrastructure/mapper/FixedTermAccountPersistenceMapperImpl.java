package com.ntt.data.bootcamp.msvc.account.infrastructure.mapper;

import com.ntt.data.bootcamp.msvc.account.domain.FixedTermAccount;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AccountHolder;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Audit;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import com.ntt.data.bootcamp.msvc.account.domain.vo.TransactionLimit;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.FixedTermAccountCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FixedTermAccountPersistenceMapperImpl implements IFixedTermAccountPersistenceMapper {

  @Override
  public FixedTermAccountCollection toEntity(FixedTermAccount fixedTermAccount) {

    BalanceCollection balanceCollection =
        new BalanceCollection(
            fixedTermAccount.getBalance().getAmount(),
            fixedTermAccount.getBalance().getCurrency().getCurrencyCode(),
            fixedTermAccount.getBalance().getTimestamp());

    List<AccountHolderCollection> accountHolderCollections =
        fixedTermAccount.getHolders().stream()
            .map(
                accountHolder ->
                    new AccountHolderCollection(
                        accountHolder.getDocumentType(),
                        accountHolder.getDocumentNumber(),
                        accountHolder.getParticipationPercentage(),
                        accountHolder.isPrimaryHolder()))
            .collect(Collectors.toList());

    TransactionLimit transactionLimit = fixedTermAccount.getTransactionLimit();
    TransactionLimitCollection transactionLimitCollection =
        new TransactionLimitCollection(
            transactionLimit.getMaxFreeTransactions(),
            transactionLimit.getFixedCommissions(),
            transactionLimit.getPercentageCommissions(),
            transactionLimit.getCurrentTransactions(),
            transactionLimit.getMonthStartDate());

    return new FixedTermAccountCollection(
        fixedTermAccount.getIdValue(),
        fixedTermAccount.getCustomerId(),
        fixedTermAccount.getCustomerType(),
        fixedTermAccount.getAccountType().name(),
        fixedTermAccount.getAccountNumber(),
        fixedTermAccount.getExternalAccountNumber(),
        fixedTermAccount.getStatus().name(),
        accountHolderCollections,
        balanceCollection,
        fixedTermAccount.getCreatedAt(),
        fixedTermAccount.getUpdatedAt(),
        fixedTermAccount.getMaturityDate(),
        fixedTermAccount.getDayOfOperation(),
        fixedTermAccount.getInterestRate(),
        fixedTermAccount.isHasPerformedMonthlyOperation(),
        transactionLimitCollection);
  }

  @Override
  public FixedTermAccount toDomain(FixedTermAccountCollection fixedTermAccountCollection) {
    AccountHolderCollection accountHolderCollection =
        fixedTermAccountCollection.getHolders().stream()
            .filter(AccountHolderCollection::isPrimary)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Primary holder not found"));
    BalanceCollection balanceCollection = fixedTermAccountCollection.getBalance();
    Balance balance =
        Balance.reconstruct(
            balanceCollection.getCurrencyCode(),
            balanceCollection.getAmount(),
            balanceCollection.getTimestamp());
    Set<AccountHolder> accountHolders =
        fixedTermAccountCollection.getHolders().stream()
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

    TransactionLimitCollection transactionLimitCollection =
        fixedTermAccountCollection.getTransactionLimit();
    TransactionLimit transactionLimit =
        TransactionLimit.reconstruct(
            transactionLimitCollection.getMaxFreeTransactions(),
            transactionLimitCollection.getFixedCommissions(),
            transactionLimitCollection.getPercentageCommissions(),
            transactionLimitCollection.getCurrentTransactions(),
            transactionLimitCollection.getMonthStartDate());

    return FixedTermAccount.reconstruct(
        fixedTermAccountCollection.getId(),
        fixedTermAccountCollection.getCustomerId(),
        fixedTermAccountCollection.getCustomerType(),
        accountHolderCollection.getDocumentType(),
        accountHolderCollection.getDocumentNumber(),
        fixedTermAccountCollection.getAccountNumber(),
        fixedTermAccountCollection.getExternalAccountNumber(),
        AccountType.valueOf(fixedTermAccountCollection.getAccountType()),
        AccountStatus.valueOf(fixedTermAccountCollection.getStatus()),
        balance,
        Audit.reconstruct(
            fixedTermAccountCollection.getCreatedAt(), fixedTermAccountCollection.getUpdatedAt()),
        accountHolders,
        fixedTermAccountCollection.getMaturityDate(),
        fixedTermAccountCollection.getDayOfOperation(),
        fixedTermAccountCollection.getInterestRate(),
        fixedTermAccountCollection.isHasPerformedMonthlyOperation(),
        transactionLimit);
  }
}
