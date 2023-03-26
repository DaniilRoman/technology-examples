package com.example.config;

import com.google.inject.AbstractModule;
import com.example.dao.AccountDAO;
import com.example.dao.TransactionDAO;
import com.example.model.Account;
import com.example.model.Transaction;
import com.example.service.AccountService;
import com.example.service.TransactionHistoryService;
import com.example.service.TransferService;

import java.util.ArrayList;
import java.util.List;

public class LightweightModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountDAO.class).to(AccountDAOTestImpl.class).asEagerSingleton();;
        bind(TransactionDAO.class).to(TransactionDAOTestImpl.class).asEagerSingleton();;
        requestStaticInjection(AccountService.class);
        requestStaticInjection(TransactionHistoryService.class);
        requestStaticInjection(TransferService.class);
    }

    private static class AccountDAOTestImpl implements AccountDAO {
        private List<Account> accounts = new ArrayList<>();

        @Override
        public void create(Account account) {
            accounts.add(account);
        }

        @Override
        public List<Account> findAll() {
            return accounts;
        }

        @Override
        public Account get(String id) {
            return accounts.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public void update(Account account) {
            int index = accounts.indexOf(account);
            accounts.set(index, account);
        }
    }

    private static class TransactionDAOTestImpl implements TransactionDAO {

        private List<Transaction> transactions = new ArrayList<>();

        @Override
        public void create(Transaction transaction) {
            transactions.add(transaction);
        }

        @Override
        public List<Transaction> findAll() {
            return transactions;
        }

        @Override
        public Transaction get(String id) {
            return transactions.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
        }
    }
}
