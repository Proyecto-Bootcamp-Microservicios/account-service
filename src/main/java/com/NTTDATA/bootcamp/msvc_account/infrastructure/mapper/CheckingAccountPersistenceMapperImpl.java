package com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper;

import com.NTTDATA.bootcamp.msvc_account.domain.CheckingAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.SavingAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.*;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.CheckingAccountCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AuthorizedSignerCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.BalanceCollection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
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

        return new CheckingAccountCollection(checkingAccount.getIdValue(), checkingAccount.getCustomerId(), checkingAccount.getCustomerType(), checkingAccount.getAccountType().name(), checkingAccount.getAccountNumber(), checkingAccount.getExternalAccountNumber(), checkingAccount.getStatus().name(), accountHolderCollections, balanceCollection, checkingAccount.getCreatedAt(), checkingAccount.getUpdatedAt(), checkingAccount.getMaintenanceFee(), checkingAccount.getNextFeeDate(), authorizedSignerCollections);
    }

    @Override
    public CheckingAccount toDomain(CheckingAccountCollection checkingAccountCollection) {
        AccountHolderCollection accountHolderCollection = checkingAccountCollection.getHolders()
                .stream().filter(AccountHolderCollection::isPrimary)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Primary holder not found"));
        BalanceCollection balanceCollection = checkingAccountCollection.getBalance();
        Balance balance = Balance.reconstruct(balanceCollection.getCurrencyCode(), balanceCollection.getAmount(), balanceCollection.getTimestamp());
        Set<AccountHolder> accountHolders = checkingAccountCollection.getHolders()
                .stream()
                .map(accountHolderCollect -> {
                    if(accountHolderCollect.isPrimary()){
                        return AccountHolder.ofPrimaryHolder(accountHolderCollect.getDocumentType(), accountHolderCollect.getDocumentNumber(), accountHolderCollect.getParticipationPercentage());
                    }else{
                        return AccountHolder.ofSecondaryHolder(accountHolderCollect.getDocumentType(), accountHolderCollect.getDocumentNumber(), accountHolderCollect.getParticipationPercentage());
                    }
                })
                .collect(Collectors.toSet());

        Set<AuthorizedSigner> authorizedSigners = checkingAccountCollection.getAuthorizedSigners()
                .stream()
                .map(authorizedSignerCollect -> AuthorizedSigner.of(authorizedSignerCollect.getDocumentType(), authorizedSignerCollect.getDocumentNumber()))
                .collect(Collectors.toSet());

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
                accountHolders, checkingAccountCollection.getMaintenanceFee(), checkingAccountCollection.getNextFeeDate(), authorizedSigners);
    }
}
