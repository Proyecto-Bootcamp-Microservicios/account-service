package com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper;

import com.NTTDATA.bootcamp.msvc_account.domain.CheckingAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.*;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.CheckingAccountCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AuthorizedSignerCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CheckingAccountPersistenceMapperImpl implements ICheckingAccountPersistenceMapper {
    @Override
    public CheckingAccountCollection toEntity(CheckingAccount checkingAccount) {
        BalanceCollection balanceCollection = new BalanceCollection(checkingAccount.getBalance().getAmount(), checkingAccount.getBalance().getCurrency().getCurrencyCode(), checkingAccount.getBalance().getTimestamp());

        List<AccountHolderCollection> accountHolderCollections = checkingAccount.getHolders()
                .stream()
                .map(accountHolder -> new AccountHolderCollection(
                        accountHolder.getDocumentType(),
                        accountHolder.getDocumentNumber(),
                        accountHolder.getParticipationPercentage(),
                        accountHolder.isPrimaryHolder())
                ).collect(Collectors.toList());

        List<AuthorizedSignerCollection> authorizedSignerCollections = checkingAccount.getSigners()
                .stream()
                .map(authorizedSigner -> new AuthorizedSignerCollection(
                        authorizedSigner.getDocumentType(),
                        authorizedSigner.getDocumentNumber())
                ).collect(Collectors.toList());

        TransactionLimit transactionLimit = checkingAccount.getTransactionLimit();
        TransactionLimitCollection transactionLimitCollection = new TransactionLimitCollection(
                transactionLimit.getMaxFreeTransactions(),
                transactionLimit.getFixedCommissions(),
                transactionLimit.getPercentageCommissions(),
                transactionLimit.getCurrentTransactions(),
                transactionLimit.getMonthStartDate());

        return new CheckingAccountCollection(checkingAccount.getIdValue(), checkingAccount.getCustomerId(), checkingAccount.getCustomerType(), checkingAccount.getAccountType().name(), checkingAccount.getAccountNumber(), checkingAccount.getExternalAccountNumber(), checkingAccount.getStatus().name(), accountHolderCollections, balanceCollection, checkingAccount.getCreatedAt(), checkingAccount.getUpdatedAt(), checkingAccount.getMaintenanceFee(), checkingAccount.getNextFeeDate(), authorizedSignerCollections, transactionLimitCollection);
    }

    @Override
    public CheckingAccount toDomain(CheckingAccountCollection checkingAccountCollection) {
        AccountHolderCollection accountHolderCollection = checkingAccountCollection.getHolders()
                .stream().filter(AccountHolderCollection::isPrimary)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Primary holder not found"));
        BalanceCollection balanceCollection = checkingAccountCollection.getBalance();
        Balance balance = Balance.reconstruct(balanceCollection.getCurrencyCode(), balanceCollection.getAmount(), balanceCollection.getTimestamp());
        checkingAccountCollection.getHolders().forEach(accountHolderCollect -> log.warn("accountHolderCollect: " + accountHolderCollect));
        Set<AccountHolder> accountHolders = checkingAccountCollection.getHolders()
                .stream()
                .map(accountHolderCollect -> {
                    if(accountHolderCollect.isPrimary()){
                        return AccountHolder.ofPrimaryHolder(accountHolderCollect.getDocumentType(), accountHolderCollect.getDocumentNumber(), accountHolderCollect.getParticipationPercentage());
                    }
                    return AccountHolder.ofSecondaryHolder(accountHolderCollect.getDocumentType(), accountHolderCollect.getDocumentNumber(), accountHolderCollect.getParticipationPercentage());
                })
                .collect(Collectors.toSet());

        accountHolders.forEach(accountHolder -> log.error(accountHolder.toString()));

        Set<AuthorizedSigner> authorizedSigners = checkingAccountCollection.getAuthorizedSigners()
                .stream()
                .map(authorizedSignerCollect -> AuthorizedSigner.of(authorizedSignerCollect.getDocumentType(), authorizedSignerCollect.getDocumentNumber()))
                .collect(Collectors.toSet());
        TransactionLimit transactionLimit = TransactionLimit.reconstruct(
                checkingAccountCollection.getTransactionLimit().getMaxFreeTransactions(),
                checkingAccountCollection.getTransactionLimit().getFixedCommissions(),
                checkingAccountCollection.getTransactionLimit().getPercentageCommissions(),
                checkingAccountCollection.getTransactionLimit().getCurrentTransactions(),
                checkingAccountCollection.getTransactionLimit().getMonthStartDate());

        return CheckingAccount.reconstruct(
                checkingAccountCollection.getId(),
                checkingAccountCollection.getCustomerId(),
                checkingAccountCollection.getCustomerType(),
                accountHolderCollection.getDocumentType(),
                accountHolderCollection.getDocumentNumber(),
                checkingAccountCollection.getAccountNumber(),
                checkingAccountCollection.getExternalAccountNumber(),
                AccountType.valueOf(checkingAccountCollection.getAccountType()),
                AccountStatus.valueOf(checkingAccountCollection.getStatus()),
                balance, Audit.reconstruct(checkingAccountCollection.getCreatedAt(), checkingAccountCollection.getUpdatedAt()),
                accountHolders, checkingAccountCollection.getMaintenanceFee(), checkingAccountCollection.getNextFeeDate(), authorizedSigners, transactionLimit);
    }
}
