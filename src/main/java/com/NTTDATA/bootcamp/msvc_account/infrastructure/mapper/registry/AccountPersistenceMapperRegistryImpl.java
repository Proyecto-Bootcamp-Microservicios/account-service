package com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper.registry;

import com.NTTDATA.bootcamp.msvc_account.domain.Account;
import com.NTTDATA.bootcamp.msvc_account.domain.CheckingAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.FixedTermAccount;
import com.NTTDATA.bootcamp.msvc_account.domain.SavingAccount;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper.ICheckingAccountPersistenceMapper;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper.IFixedTermAccountPersistenceMapper;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.mapper.ISavingAccountPersistenceMapper;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.AccountCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.CheckingAccountCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.FixedTermAccountCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.SavingAccountCollection;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class AccountPersistenceMapperRegistryImpl implements IAccountPersistenceMapperRegistry {

    private final Map<Class<?>, Function<AccountCollection, Account>> registryMapToDomain = new HashMap<>();
    private final Map<Class<?>, Function<Account, AccountCollection>> registryMapToEntity = new HashMap<>();

    public AccountPersistenceMapperRegistryImpl(
            ICheckingAccountPersistenceMapper checkingAccountPersistenceMapper,
            ISavingAccountPersistenceMapper savingAccountPersistenceMapper,
            IFixedTermAccountPersistenceMapper fixedTermAccountPersistenceMapper
    ) {
        registryMapToDomain.put(CheckingAccountCollection.class, acc -> checkingAccountPersistenceMapper.toDomain((CheckingAccountCollection) acc));
        registryMapToEntity.put(CheckingAccount.class, acc -> checkingAccountPersistenceMapper.toEntity((CheckingAccount) acc));

        registryMapToDomain.put(SavingAccountCollection.class, acc -> savingAccountPersistenceMapper.toDomain((SavingAccountCollection) acc));
        registryMapToEntity.put(SavingAccount.class, acc -> savingAccountPersistenceMapper.toEntity((SavingAccount) acc));
        registryMapToDomain.put(FixedTermAccountCollection.class, acc -> fixedTermAccountPersistenceMapper.toDomain((FixedTermAccountCollection) acc));
        registryMapToEntity.put(FixedTermAccount.class, acc -> fixedTermAccountPersistenceMapper.toEntity((FixedTermAccount) acc));
    }

    @Override
    public Account toDomain(AccountCollection accountCollection) {
        Function<AccountCollection, Account> mapper = registryMapToDomain.get(accountCollection.getClass());
        return mapper != null ? mapper.apply(accountCollection) : null;
    }

    @Override
    public AccountCollection toEntity(Account account) {
        Function<Account, AccountCollection> mapper = registryMapToEntity.get(account.getClass());
        return mapper != null ? mapper.apply(account) : null;
    }


}
